package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.party;

import com.rbkmoney.damsel.domain.Suspension;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.domain.tables.pojos.Party;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractPartyManagementHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PartySuspensionHandler extends AbstractPartyManagementHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PartyDao partyDao;

    private final Filter filter;

    public PartySuspensionHandler(PartyDao partyDao) {
        this.partyDao = partyDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "party_suspension",
                new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, Event event) {
        long eventId = event.getId();
        Suspension partySuspension = change.getPartySuspension();
        String partyId = event.getSource().getPartyId();
        log.info("Start party suspension handling, eventId={}, partyId={}", eventId, partyId);
        Party partySource = partyDao.get(partyId);
        if (partySource == null) {
            throw new NotFoundException(String.format("Party not found, partyId='%s'", partyId));
        }
        partySource.setId(null);
        partySource.setWtime(null);
        partySource.setEventId(eventId);
        partySource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        com.rbkmoney.newway.domain.enums.Suspension suspension = TypeUtil.toEnumField(partySuspension.getSetField().getFieldName(), com.rbkmoney.newway.domain.enums.Suspension.class);
        if (suspension == null) {
            throw new IllegalArgumentException("Illegal partySource suspension: " + partySuspension);
        }
        partySource.setSuspension(suspension);
        if (partySuspension.isSetActive()) {
            partySource.setSuspensionActiveSince(TypeUtil.stringToLocalDateTime(partySuspension.getActive().getSince()));
            partySource.setSuspensionSuspendedSince(null);
        } else if (partySuspension.isSetSuspended()) {
            partySource.setSuspensionActiveSince(null);
            partySource.setSuspensionSuspendedSince(TypeUtil.stringToLocalDateTime(partySuspension.getSuspended().getSince()));
        }
        partyDao.updateNotCurrent(partyId);
        partyDao.save(partySource);
        log.info("Party suspension has been saved, eventId={}, partyId={}", eventId, partyId);
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
