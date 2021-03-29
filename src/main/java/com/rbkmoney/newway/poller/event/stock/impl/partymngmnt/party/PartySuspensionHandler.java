package com.rbkmoney.newway.poller.event.stock.impl.partymngmnt.party;

import com.rbkmoney.damsel.domain.Suspension;
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
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
import com.rbkmoney.newway.poller.event.stock.impl.partymngmnt.PartyManagementHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartySuspensionHandler implements PartyManagementHandler {

    public static final String PARTY_SUSPENSION = "party_suspension";

    private final PartyDao partyDao;
    private final MachineEventCopyFactory<Party, Integer> partyIntegerMachineEventCopyFactory;

    @Getter
    private final Filter filter =
            new PathConditionFilter(new PathConditionRule(PARTY_SUSPENSION, new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        Suspension partySuspension = change.getPartySuspension();
        String partyId = event.getSourceId();
        log.info("Start {} handling, eventId={}, partyId={}, changeId={}", PARTY_SUSPENSION, sequenceId, partyId,
                changeId);
        Party partyOld = partyDao.get(partyId);
        Party partyNew = partyIntegerMachineEventCopyFactory.create(event, sequenceId, changeId, partyOld, null);

        partyNew.setSuspension(
                TBaseUtil.unionFieldToEnum(partySuspension, com.rbkmoney.newway.domain.enums.Suspension.class));
        if (partySuspension.isSetActive()) {
            partyNew.setSuspensionActiveSince(TypeUtil.stringToLocalDateTime(partySuspension.getActive().getSince()));
            partyNew.setSuspensionSuspendedSince(null);
        } else if (partySuspension.isSetSuspended()) {
            partyNew.setSuspensionActiveSince(null);
            partyNew.setSuspensionSuspendedSince(
                    TypeUtil.stringToLocalDateTime(partySuspension.getSuspended().getSince()));
        }

        partyDao.saveWithUpdateCurrent(partyNew, partyOld.getId(), PARTY_SUSPENSION);
    }

}
