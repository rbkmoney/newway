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
import com.rbkmoney.newway.domain.tables.pojos.ContractAdjustment;
import com.rbkmoney.newway.domain.tables.pojos.Party;
import com.rbkmoney.newway.domain.tables.pojos.PayoutTool;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractPartyManagementHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        if (partySource == null) {
            throw new NotFoundException(String.format("Party not found, partyId='%s'", partyId));
        }
        partySource.setId(null);
        partySource.setWtime(null);
        partySource.setSequenceId(sequenceId);
        partySource.setChangeId(changeId);
        partySource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        long revision = partyRevisionChanged.getRevision();
        partySource.setRevision(revision);
        partySource.setRevisionChangedAt(TypeUtil.stringToLocalDateTime(partyRevisionChanged.getTimestamp()));
        partyDao.save(partySource);
        partyDao.switchCurrent(partyId);
        updateContractorsRevision(event, partyId, revision, changeId);
        updateContractsRevision(event, partyId, revision, changeId);
        updateShopsRevision(event, partyId, revision, changeId);
        log.info("Party revision changed has been saved, eventId={}, partyId={}", sequenceId, partyId);
    }

    private void updateShopsRevision(MachineEvent event, String partyId, long revision, Integer changeId) {
        shopDao.getByPartyId(partyId).forEach(shopSource -> {
            String shopId = shopSource.getShopId();
            shopSource.setId(null);
            shopSource.setWtime(null);
            shopSource.setChangeId(changeId);
            shopSource.setSequenceId(event.getEventId());
            shopSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            shopSource.setRevision(revision);
            shopDao.save(shopSource);
            shopDao.switchCurrent(partyId, shopId);
            log.info("Shop revision has been saved, eventId={}, partyId={}, shopId={}", event.getEventId(), partyId, shopId);
        });
    }

    private void updateContractorsRevision(MachineEvent event, String partyId, long revision, Integer changeId) {
        contractorDao.getByPartyId(partyId).forEach(contractorSource -> {
            String contractorId = contractorSource.getContractorId();
            contractorSource.setId(null);
            contractorSource.setWtime(null);
            contractorSource.setSequenceId(event.getEventId());
            contractorSource.setChangeId(changeId);
            contractorSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            contractorSource.setRevision(revision);
            contractorDao.switchCurrent(partyId, contractorId);
            contractorDao.save(contractorSource);
            log.info("Contractor revision has been saved, eventId={}, partyId={}, contractorId={}", event.getEventId(), partyId, contractorId);
        });
    }

    private void updateContractsRevision(MachineEvent event, String partyId, long revision, Integer changeId) {
        contractDao.getByPartyId(partyId).forEach(contractSource -> {
            Long contractSourceId = contractSource.getId();
            String contractId = contractSource.getContractId();
            contractSource.setId(null);
            contractSource.setWtime(null);
            contractSource.setSequenceId(event.getEventId());
            contractSource.setChangeId(changeId);
            contractSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            contractSource.setRevision(revision);
            contractDao.switchCurrent(partyId, contractId);
            long cntrctId = contractDao.save(contractSource);

            List<ContractAdjustment> adjustments = contractAdjustmentDao.getByCntrctId(contractSourceId);
            adjustments.forEach(a -> {
                a.setId(null);
                a.setCntrctId(cntrctId);
            });
            contractAdjustmentDao.save(adjustments);

            List<PayoutTool> payoutTools = payoutToolDao.getByCntrctId(contractSourceId);
            payoutTools.forEach(pt -> {
                pt.setId(null);
                pt.setCntrctId(cntrctId);
            });
            payoutToolDao.save(payoutTools);
            log.info("Contract revision has been saved, eventId={}, partyId={}, contractId={}", event.getEventId(), partyId, contractId);
        });
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
