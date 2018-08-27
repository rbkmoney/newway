package com.rbkmoney.newway.poller.handler.impl.party_mngmnt.party;

import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyCreated;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.domain.enums.Blocking;
import com.rbkmoney.newway.domain.enums.Suspension;
import com.rbkmoney.newway.domain.tables.pojos.Party;
import com.rbkmoney.newway.poller.handler.impl.party_mngmnt.AbstractPartyManagementHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class PartyCreatedHandler extends AbstractPartyManagementHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PartyDao partyDao;

    private final Filter filter;

    public PartyCreatedHandler(PartyDao partyDao) {
        this.partyDao = partyDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "party_created",
                new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, Event event) {
        long eventId = event.getId();
        PartyCreated partyCreated = change.getPartyCreated();
        String partyId = partyCreated.getId();
        log.info("Start party created handling, eventId={}, partyId={}", eventId, partyId);
        Party party = new Party();
        party.setEventId(eventId);
        party.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        party.setPartyId(partyId);
        party.setContactInfoEmail(partyCreated.getContactInfo().getEmail());
        party.setCreatedAt(TypeUtil.stringToLocalDateTime(partyCreated.getCreatedAt()));
        party.setBlocking(Blocking.unblocked);
        party.setBlockingUnblockedReason("");
        party.setBlockingUnblockedSince(LocalDateTime.now(ZoneOffset.UTC));
        party.setSuspension(Suspension.active);
        party.setSuspensionActiveSince(LocalDateTime.now(ZoneOffset.UTC));
        party.setRevision(0L);
        party.setRevisionChangedAt(LocalDateTime.now(ZoneOffset.UTC));
        partyDao.save(party);
        log.info("Party has been saved, eventId={}, partyId={}", eventId, partyId);
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
