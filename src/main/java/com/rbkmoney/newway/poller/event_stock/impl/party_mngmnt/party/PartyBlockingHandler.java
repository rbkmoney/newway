package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.party;

import com.rbkmoney.damsel.domain.Blocking;
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
public class PartyBlockingHandler extends AbstractPartyManagementHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PartyDao partyDao;

    private final Filter filter;

    public PartyBlockingHandler(PartyDao partyDao) {
        this.partyDao = partyDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "party_blocking",
                new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, Event event) {
        long eventId = event.getId();
        Blocking partyBlocking = change.getPartyBlocking();
        String partyId = event.getSource().getPartyId();
        log.info("Start party blocking handling, eventId={}, partyId={}", eventId, partyId);
        Party partySource = partyDao.get(partyId);
        if (partySource == null) {
            throw new NotFoundException(String.format("Party not found, partyId='%s'", partyId));
        }
        partySource.setId(null);
        partySource.setWtime(null);
        partySource.setEventId(eventId);
        partySource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        com.rbkmoney.newway.domain.enums.Blocking blocking = TypeUtil.toEnumField(partyBlocking.getSetField().getFieldName(), com.rbkmoney.newway.domain.enums.Blocking.class);
        if (blocking == null) {
            throw new IllegalArgumentException("Illegal party blocking: " + partyBlocking);
        }
        partySource.setBlocking(blocking);
        if (partyBlocking.isSetUnblocked()) {
            partySource.setBlockingUnblockedReason(partyBlocking.getUnblocked().getReason());
            partySource.setBlockingUnblockedSince(TypeUtil.stringToLocalDateTime(partyBlocking.getUnblocked().getSince()));
            partySource.setBlockingBlockedReason(null);
            partySource.setBlockingBlockedSince(null);
        } else if (partyBlocking.isSetBlocked()) {
            partySource.setBlockingUnblockedReason(null);
            partySource.setBlockingUnblockedSince(null);
            partySource.setBlockingBlockedReason(partyBlocking.getBlocked().getReason());
            partySource.setBlockingBlockedSince(TypeUtil.stringToLocalDateTime(partyBlocking.getBlocked().getSince()));
        }
        partyDao.updateNotCurrent(partyId);
        partyDao.save(partySource);
        log.info("Party blocking has been saved, eventId={}, partyId={}", eventId, partyId);
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
