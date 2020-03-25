package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.party;

import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyRevisionChanged;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.party.iface.*;
import com.rbkmoney.newway.domain.tables.pojos.Party;
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
        updateContractorsRevision(event, partyId, revision, changeId);
        updateContractsRevision(event, partyId, revision, changeId);
        updateShopsRevision(event, partyId, revision, changeId);
        log.info("Party revision changed has been saved, eventId={}, partyId={}", sequenceId, partyId);
    }

    private void updateShopsRevision(MachineEvent event, String partyId, long revision, Integer changeId) {
        shopDao.getByPartyId(partyId).forEach(shopSource -> {
            String shopId = shopSource.getShopId();
            long sequenceId = event.getEventId();
            Long oldEventId = shopSource.getId();
            ShopUtil.resetBaseFields(event, changeId, sequenceId, shopSource);
            shopSource.setRevision(revision);
            shopDao.saveWithUpdateCurrent(shopSource, oldEventId, "revision");
        });
    }

    private void updateContractorsRevision(MachineEvent event, String partyId, long revision, Integer changeId) {
        contractorDao.getByPartyId(partyId).forEach(contractorSource -> {
            Long oldId = contractorSource.getId();
            long sequenceId = event.getEventId();
            ContractorUtil.resetBaseFields(event, sequenceId, contractorSource);
            contractorSource.setRevision(revision);
            contractorDao.save(contractorSource)
                    .ifPresentOrElse(
                            saveResult -> {
                                contractorDao.updateNotCurrent(oldId);
                                log.info("Contractor revision has been saved, sequenceId={}, partyId={}, changeId={}", sequenceId, partyId, changeId);
                            },
                            () -> log.info("Contractor revision duplicated, sequenceId={}, partyId={}, changeId={}", sequenceId, partyId, changeId)
                    );
        });
    }

    private void updateContractsRevision(MachineEvent event, String partyId, long revision, Integer changeId) {
        contractDao.getByPartyId(partyId).forEach(contractSource -> {
            Long contractSourceId = contractSource.getId();
            String contractId = contractSource.getContractId();
            long sequenceId = event.getEventId();
            ContractUtil.resetBaseFields(event, changeId, sequenceId, contractSource);
            contractSource.setRevision(revision);

            contractDao.save(contractSource)
                    .ifPresentOrElse(
                            dbContractId -> {
                                contractDao.updateNotCurrent(contractSourceId);
                                contractReferenceService.updateContractReference(contractSourceId, dbContractId);
                                log.info("Contract revision has been saved, eventId={}, partyId={}, contractId={}", event.getEventId(), partyId, contractId);
                            },
                            () -> log.info("Contract revision duplicated, sequenceId={}, partyId={}, contractId={}, changeId={}",
                                    sequenceId, partyId, contractId, changeId)
                    );
        });
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
