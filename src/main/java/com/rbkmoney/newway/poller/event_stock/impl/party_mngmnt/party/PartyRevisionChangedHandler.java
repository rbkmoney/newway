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
import com.rbkmoney.newway.domain.tables.pojos.*;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractPartyManagementHandler;
import com.rbkmoney.newway.util.PartyUtil;
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
    private final ContractorDao contractorDao;
    private final ContractDao contractDao;
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
        Long oldId = partySource.getId();
        PartyUtil.resetBaseFields(event, changeId, sequenceId, partySource);
        long revision = partyRevisionChanged.getRevision();
        partySource.setRevision(revision);
        partySource.setRevisionChangedAt(TypeUtil.stringToLocalDateTime(partyRevisionChanged.getTimestamp()));

        partyDao.save(partySource)
                .ifPresentOrElse(
                        aLong -> {
                            partyDao.updateNotCurrent(oldId);
                            updatePartyReferences(partyId, revision);
                            log.info("Party revision changed has been saved, sequenceId={}, partyId={}, changeId={}", sequenceId, partyId, changeId);
                        },
                        () -> log.info("Party revision changed duplicated, sequenceId={}, partyId={}, changeId={}", sequenceId, partyId, changeId)
                );
    }

    private void updatePartyReferences(String partyId, long revision) {
        log.info("Start to update revisions, partyId={}, revision={}", partyId, revision);
        contractorDao.updateRevision(partyId, revision);
        log.info("Contractors revisions has been updated, partyId={}, revision={}", partyId, revision);
        contractDao.updateRevision(partyId, revision);
        log.info("Contracts revision has been updated, partyId={}, revision={}", partyId, revision);
        shopDao.updateRevision(partyId, revision);
        log.info("Shops revisions has been updated, partyId={}, revision={}", partyId, revision);
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
