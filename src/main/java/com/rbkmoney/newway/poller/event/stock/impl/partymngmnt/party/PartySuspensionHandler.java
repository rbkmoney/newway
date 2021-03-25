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
import com.rbkmoney.newway.poller.event.stock.impl.partymngmnt.AbstractPartyManagementHandler;
import com.rbkmoney.newway.util.PartyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PartySuspensionHandler extends AbstractPartyManagementHandler {

    public static final String PARTY_SUSPENSION = "party_suspension";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PartyDao partyDao;

    private final Filter filter;

    public PartySuspensionHandler(PartyDao partyDao) {
        this.partyDao = partyDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                PARTY_SUSPENSION,
                new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        Suspension partySuspension = change.getPartySuspension();
        String partyId = event.getSourceId();
        log.info("Start {} handling, eventId={}, partyId={}, changeId={}", PARTY_SUSPENSION, sequenceId, partyId,
                changeId);
        Party partySource = partyDao.get(partyId);
        PartyUtil.resetBaseFields(event, changeId, sequenceId, partySource);

        partySource.setSuspension(
                TBaseUtil.unionFieldToEnum(partySuspension, com.rbkmoney.newway.domain.enums.Suspension.class));
        if (partySuspension.isSetActive()) {
            partySource
                    .setSuspensionActiveSince(TypeUtil.stringToLocalDateTime(partySuspension.getActive().getSince()));
            partySource.setSuspensionSuspendedSince(null);
        } else if (partySuspension.isSetSuspended()) {
            partySource.setSuspensionActiveSince(null);
            partySource.setSuspensionSuspendedSince(
                    TypeUtil.stringToLocalDateTime(partySuspension.getSuspended().getSince()));
        }

        Long oldId = partySource.getId();
        partyDao.saveWithUpdateCurrent(partySource, oldId, PARTY_SUSPENSION);
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
