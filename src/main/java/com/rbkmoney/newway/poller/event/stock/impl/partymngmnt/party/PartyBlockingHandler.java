package com.rbkmoney.newway.poller.event.stock.impl.partymngmnt.party;

import com.rbkmoney.damsel.domain.Blocking;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.domain.tables.pojos.Party;
import com.rbkmoney.newway.poller.event.stock.impl.partymngmnt.AbstractPartyManagementHandler;
import com.rbkmoney.newway.util.PartyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("VariableDeclarationUsageDistance")
public class PartyBlockingHandler extends AbstractPartyManagementHandler {

    private final PartyDao partyDao;
    private final Filter filter = new PathConditionFilter(new PathConditionRule(
            "party_blocking",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        Blocking partyBlocking = change.getPartyBlocking();
        String partyId = event.getSourceId();
        log.info("Start party blocking handling, sequenceId={}, partyId={}, changeId={}", sequenceId, partyId,
                changeId);
        Party partySource = partyDao.get(partyId);
        Long oldId = partySource.getId();
        PartyUtil.resetBaseFields(event, changeId, sequenceId, partySource);

        partySource.setBlocking(
                TBaseUtil.unionFieldToEnum(partyBlocking, com.rbkmoney.newway.domain.enums.Blocking.class));
        if (partyBlocking.isSetUnblocked()) {
            partySource.setBlockingUnblockedReason(partyBlocking.getUnblocked().getReason());
            partySource
                    .setBlockingUnblockedSince(TypeUtil.stringToLocalDateTime(partyBlocking.getUnblocked().getSince()));
            partySource.setBlockingBlockedReason(null);
            partySource.setBlockingBlockedSince(null);
        } else if (partyBlocking.isSetBlocked()) {
            partySource.setBlockingUnblockedReason(null);
            partySource.setBlockingUnblockedSince(null);
            partySource.setBlockingBlockedReason(partyBlocking.getBlocked().getReason());
            partySource.setBlockingBlockedSince(TypeUtil.stringToLocalDateTime(partyBlocking.getBlocked().getSince()));
        }

        partyDao.saveWithUpdateCurrent(partySource, oldId, "blocking");
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
