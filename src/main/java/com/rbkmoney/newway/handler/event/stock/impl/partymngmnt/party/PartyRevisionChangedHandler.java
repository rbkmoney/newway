package com.rbkmoney.newway.handler.event.stock.impl.partymngmnt.party;

import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyRevisionChanged;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.dao.party.iface.RevisionDao;
import com.rbkmoney.newway.domain.tables.pojos.Party;
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
import com.rbkmoney.newway.handler.event.stock.impl.partymngmnt.PartyManagementHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Component
@RequiredArgsConstructor
public class PartyRevisionChangedHandler implements PartyManagementHandler {

    private final PartyDao partyDao;
    private final RevisionDao revisionDao;
    private final MachineEventCopyFactory<Party, Integer> partyIntegerMachineEventCopyFactory;

    @Getter
    private final Filter filter =
            new PathConditionFilter(new PathConditionRule("revision_changed", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        PartyRevisionChanged partyRevisionChanged = change.getRevisionChanged();
        String partyId = event.getSourceId();
        log.info("Start partySource revision changed handling, eventId={}, partyId={}, changeId={}",
                sequenceId, partyId, changeId);

        Party partyOld = partyDao.get(partyId);
        long revision = partyRevisionChanged.getRevision();
        Party partyNew = partyIntegerMachineEventCopyFactory.create(event, sequenceId, changeId, partyOld, null);
        partyNew.setRevision(revision);
        partyNew.setRevisionChangedAt(TypeUtil.stringToLocalDateTime(partyRevisionChanged.getTimestamp()));

        partyDao.save(partyNew)
                .ifPresentOrElse(
                        anLong -> {
                            partyDao.updateNotCurrent(partyOld.getId());
                            updatePartyReferences(partyId, revision);
                            log.info("Party revision changed has been saved, sequenceId={}, partyId={}, changeId={}",
                                    sequenceId, partyId, changeId);
                        },
                        () -> log.info("Party revision changed duplicated, sequenceId={}, partyId={}, changeId={}",
                                sequenceId, partyId, changeId)
                );
    }

    private void updatePartyReferences(String partyId, long revision) {
        log.info("Start to save revisions, partyId={}, revision={}", partyId, revision);
        revisionDao.saveContractorsRevision(partyId, revision);
        log.info("Contractors revisions has been saved, partyId={}, revision={}", partyId, revision);
        revisionDao.saveContractsRevision(partyId, revision);
        log.info("Contracts revision has been saved, partyId={}, revision={}", partyId, revision);
        revisionDao.saveShopsRevision(partyId, revision);
        log.info("Shops revisions has been saved, partyId={}, revision={}", partyId, revision);
    }

}
