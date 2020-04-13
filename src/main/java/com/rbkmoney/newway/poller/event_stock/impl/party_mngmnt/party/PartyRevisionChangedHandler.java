package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.party;

import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyRevisionChanged;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.invoicing.impl.ContractIdsGeneratorDaoImpl;
import com.rbkmoney.newway.dao.party.iface.*;
import com.rbkmoney.newway.domain.tables.pojos.*;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractPartyManagementHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PartyRevisionChangedHandler extends AbstractPartyManagementHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PartyDao partyDao;
    private final ContractDao contractDao;
    private final ContractAdjustmentDao contractAdjustmentDao;
    private final PayoutToolDao payoutToolDao;
    private final ContractorDao contractorDao;
    private final ShopDao shopDao;
    private final ContractIdsGeneratorDaoImpl contractIdsGeneratorDao;

    private final Filter filter;

    public PartyRevisionChangedHandler(PartyDao partyDao, ContractDao contractDao, ContractAdjustmentDao contractAdjustmentDao, PayoutToolDao payoutToolDao, ContractorDao contractorDao, ShopDao shopDao, ContractIdsGeneratorDaoImpl contractIdsGeneratorDao) {
        this.partyDao = partyDao;
        this.contractDao = contractDao;
        this.contractAdjustmentDao = contractAdjustmentDao;
        this.payoutToolDao = payoutToolDao;
        this.contractorDao = contractorDao;
        this.shopDao = shopDao;
        this.contractIdsGeneratorDao = contractIdsGeneratorDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "revision_changed",
                new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, Event event, Integer changeId) {
        long eventId = event.getId();
        PartyRevisionChanged partyRevisionChanged = change.getRevisionChanged();
        String partyId = event.getSource().getPartyId();
        log.info("Start partySource revision changed handling, eventId={}, partyId={}", eventId, partyId);
        Party partySource = partyDao.get(partyId);
        if (partySource == null) {
            throw new NotFoundException(String.format("Party not found, partyId='%s'", partyId));
        }
        partySource.setId(null);
        partySource.setWtime(null);
        partySource.setEventId(eventId);
        partySource.setSequenceId(event.getSequence());
        partySource.setChangeId(changeId);
        partySource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        long revision = partyRevisionChanged.getRevision();
        partySource.setRevision(revision);
        partySource.setRevisionChangedAt(TypeUtil.stringToLocalDateTime(partyRevisionChanged.getTimestamp()));
        partyDao.updateNotCurrent(partyId);
        partyDao.save(partySource);
        updateContractorsRevision(event, partyId, revision);
        updateContractsRevision(event, partyId, revision);
        updateShopsRevision(event, partyId, revision);
        log.info("Party revision changed has been saved, eventId={}, partyId={}", eventId, partyId);
    }

    private void updateShopsRevision(Event event, String partyId, long revision) {
        List<Shop> shops = shopDao.getByPartyId(partyId);
        shops.forEach(shopSource -> {
            shopSource.setId(null);
            shopSource.setWtime(null);
            shopSource.setEventId(event.getId());
            shopSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            shopSource.setRevision(revision);
        });

        List<String> shopIds = shops.stream().map(Shop::getShopId).collect(Collectors.toList());
        shopDao.updateNotCurrent(partyId, shopIds);
        log.info("Shops current has been updated, eventId={}, partyId={}, count={}",
                event.getId(), partyId, shops.size());
        shopDao.saveBatch(shops);
        log.info("Shops revisions has been saved, eventId={}, partyId={}, count={}, shopIds={}",
                event.getId(), partyId, shops.size(), shopIds);
    }

    private void updateContractorsRevision(Event event, String partyId, long revision) {
        List<Contractor> contractors = contractorDao.getByPartyId(partyId);
        contractors.forEach(contractorSource -> {
            contractorSource.setId(null);
            contractorSource.setWtime(null);
            contractorSource.setEventId(event.getId());
            contractorSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            contractorSource.setRevision(revision);
        });

        List<String> contractorIds = contractors.stream().map(Contractor::getContractorId).collect(Collectors.toList());
        contractorDao.updateNotCurrent(partyId, contractorIds);
        log.info("Contractors current has been updated, eventId={}, partyId={}, count={}",
                event.getId(), partyId, contractors.size());
        contractorDao.saveBatch(contractors);
        log.info("Contractors revisions has been saved, eventId={}, partyId={}, count={}, contractorIds={}",
                event.getId(), partyId, contractors.size(), contractorIds);
    }

    private void updateContractsRevision(Event event, String partyId, long revision) {
        List<Contract> contracts = contractDao.getByPartyId(partyId);
        List<Long> ids = contractIdsGeneratorDao.get(contracts.size());
        List<ContractAdjustment> allAdjustments = new ArrayList<>();
        List<PayoutTool> allPayoutTools = new ArrayList<>();
        for (int i = 0; i < contracts.size(); ++ i) {
            Contract contractSource = contracts.get(i);
            Long contractSourceId = contractSource.getId();
            String contractId = contractSource.getContractId();
            Long cntrctId = ids.get(i);
            contractSource.setId(cntrctId);
            contractSource.setWtime(null);
            contractSource.setEventId(event.getId());
            contractSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            contractSource.setRevision(revision);

            List<ContractAdjustment> adjustments = contractAdjustmentDao.getByCntrctId(contractSourceId);
            adjustments.forEach(a -> {
                a.setId(null);
                a.setCntrctId(cntrctId);
            });
            allAdjustments.addAll(adjustments);

            List<PayoutTool> payoutTools = payoutToolDao.getByCntrctId(contractSourceId);
            payoutTools.forEach(pt -> {
                pt.setId(null);
                pt.setCntrctId(cntrctId);
            });
            allPayoutTools.addAll(payoutTools);
        }
        log.info("Contracts has been prepared for saving, eventId={}, partyId={}", event.getId(), partyId);

        List<String> contractIds = contracts.stream().map(Contract::getContractId).collect(Collectors.toList());
        contractDao.updateNotCurrent(partyId, contractIds);
        log.info("Contracts current has been updated, eventId={}, partyId={}, count={}",
                event.getId(), partyId, contracts.size());
        contractDao.saveBatch(contracts);
        log.info("Contracts has been saved, eventId={}, partyId={}, count={}",
                event.getId(), partyId, contracts.size());
        contractAdjustmentDao.save(allAdjustments);
        log.info("ContractAdjustments has been saved, eventId={}, partyId={}, count={}",
                event.getId(), partyId, allAdjustments.size());
        payoutToolDao.save(allPayoutTools);
        log.info("Contracts revision has been saved, eventId={}, partyId={}, contractId={}", event.getId(), partyId, contractIds);
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
