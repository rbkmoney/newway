package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.party;

import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyRevisionChanged;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.invoicing.impl.ContractIdsGeneratorDaoImpl;
import com.rbkmoney.newway.dao.party.iface.*;
import com.rbkmoney.newway.domain.tables.pojos.*;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractPartyManagementHandler;
import com.rbkmoney.newway.service.ContractReferenceService;
import com.rbkmoney.newway.util.ContractUtil;
import com.rbkmoney.newway.util.ContractorUtil;
import com.rbkmoney.newway.util.PartyUtil;
import com.rbkmoney.newway.util.ShopUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartyRevisionChangedHandler extends AbstractPartyManagementHandler {

    private final PartyDao partyDao;
    private final ContractDao contractDao;
    private final ContractAdjustmentDao contractAdjustmentDao;
    private final PayoutToolDao payoutToolDao;
    private final ContractorDao contractorDao;
    private final ShopDao shopDao;
    private final ContractReferenceService contractReferenceService;
    private final ContractIdsGeneratorDaoImpl contractIdsGeneratorDao;

    private final Filter filter = new PathConditionFilter(new PathConditionRule(
            "revision_changed",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        PartyRevisionChanged partyRevisionChanged = change.getRevisionChanged();
        String partyId = event.getSourceId();
        log.info("Start partySource revision changed handling, eventId={}, partyId={}, changeId={}", sequenceId, partyId, changeId);

        Party partySource = partyDao.get(partyId);
        Long oldId = partySource.getId();
        PartyUtil.resetBaseFields(event, changeId, sequenceId, partySource);
        long revision = partyRevisionChanged.getRevision();
        partySource.setRevision(revision);
        partySource.setRevisionChangedAt(TypeUtil.stringToLocalDateTime(partyRevisionChanged.getTimestamp()));

        partyDao.save(partySource)
                .ifPresentOrElse(
                        aLong -> {
                            partyDao.updateNotCurrent(oldId);
                            updatePartyReferences(event, changeId, sequenceId, partyId, revision);
                            log.info("Party revision changed has been saved, sequenceId={}, partyId={}, changeId={}", sequenceId, partyId, changeId);
                        },
                        () -> log.info("Party revision changed duplicated, sequenceId={}, partyId={}, changeId={}", sequenceId, partyId, changeId)
                );
    }

    private void updatePartyReferences(MachineEvent event, Integer changeId, long sequenceId, String partyId, long revision) {
        updateContractorsRevision(event, partyId, revision);
        updateContractsRevision(event, partyId, revision, changeId);
        updateShopsRevision(event, partyId, revision, changeId);
        log.info("Party revision changed has been saved, eventId={}, partyId={}", sequenceId, partyId);
    }

    private void updateShopsRevision(MachineEvent event, String partyId, long revision, Integer changeId) {
        List<Shop> shops = shopDao.getByPartyId(partyId);
        List<Long> ids = new ArrayList<>();
        shops.forEach(shopSource -> {
            long sequenceId = event.getEventId();
            ids.add(shopSource.getId());
            ShopUtil.resetBaseFields(event, changeId, sequenceId, shopSource, shopSource.getClaimEffectId());
            shopSource.setRevision(revision);
        });
        log.info("Shops has been prepared for saving, eventId={}, partyId={}, count={}",
                event.getEventId(), partyId, shops.size());
        shopDao.saveBatch(shops);
        List<String> shopIds = shops.stream()
                .map(Shop::getShopId)
                .collect(Collectors.toList());
        shopDao.switchCurrent(shopIds, partyId);
        log.info("Shops revisions has been saved, eventId={}, partyId={}, count={}",
                event.getEventId(), partyId, shops.size());
    }

    private void updateContractorsRevision(MachineEvent event, String partyId, long revision) {
        List<Contractor> contractors = contractorDao.getByPartyId(partyId);
        List<Long> contractorIds = new ArrayList<>();
        contractors.forEach(contractorSource -> {
            contractorIds.add(contractorSource.getId());
            long sequenceId = event.getEventId();
            ContractorUtil.resetBaseFields(event, sequenceId, contractorSource, contractorSource.getClaimEffectId());
            contractorSource.setRevision(revision);
        });
        log.info("Contractors has been prepared for saving, eventId={}, partyId={}, count={}",
                event.getEventId(), partyId, contractors.size());
        contractorDao.saveBatch(contractors);
        List<String> ids = contractors.stream()
                .map(Contractor::getContractorId)
                .collect(Collectors.toList());
        contractorDao.switchCurrent(ids, partyId);
        log.info("Contractors revisions has been saved, eventId={}, partyId={}, count={}",
                event.getEventId(), partyId, contractors.size());
    }

    private void updateContractsRevision(MachineEvent event, String partyId, long revision, Integer changeId) {
        List<Contract> contracts = contractDao.getByPartyId(partyId);
        List<Long> contractIds = new ArrayList<>();
        List<Long> ids = contractIdsGeneratorDao.get(contracts.size());
        List<ContractAdjustment> allAdjustments = new ArrayList<>();
        List<PayoutTool> allPayoutTools = new ArrayList<>();
        long eventId = event.getEventId();
        for (int i = 0; i < contracts.size(); ++i) {
            Contract contractSource = contracts.get(i);
            Long contractSourceId = contractSource.getId();
            contractIds.add(contractSourceId);
            Long contractId = ids.get(i);
            long sequenceId = event.getEventId();
            ContractUtil.resetBaseFields(event, changeId, sequenceId, contractSource, contractSource.getClaimEffectId());
            contractSource.setId(contractId);
            contractSource.setRevision(revision);

            List<ContractAdjustment> adjustments = contractAdjustmentDao.getByCntrctId(contractSourceId);
            adjustments.forEach(a -> {
                a.setId(null);
                a.setCntrctId(contractId);
            });
            allAdjustments.addAll(adjustments);

            List<PayoutTool> payoutTools = payoutToolDao.getByCntrctId(contractSourceId);
            payoutTools.forEach(pt -> {
                pt.setId(null);
                pt.setCntrctId(contractId);
            });
            allPayoutTools.addAll(payoutTools);
        }
        log.info("Contracts has been prepared for saving, eventId={}, partyId={}", eventId, partyId);
        contractDao.saveBatch(contracts);
        List<String> contIds = contracts.stream()
                .map(Contract::getContractId)
                .collect(Collectors.toList());
        contractDao.switchCurrent(contIds, partyId);
        log.info("Contracts has been saved, eventId={}, partyId={}, count={}",
                eventId, partyId, contracts.size());
        contractAdjustmentDao.save(allAdjustments);
        log.info("ContractAdjustments has been saved, eventId={}, partyId={}, count={}",
                eventId, partyId, allAdjustments.size());
        payoutToolDao.save(allPayoutTools);
        log.info("PayoutTools has been saved, eventId={}, partyId={}, count={}",
                eventId, partyId, allPayoutTools.size());
        log.info("Contracts revision has been saved, eventId={}, partyId={}, count={}",
                eventId, partyId, allPayoutTools.size());
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
