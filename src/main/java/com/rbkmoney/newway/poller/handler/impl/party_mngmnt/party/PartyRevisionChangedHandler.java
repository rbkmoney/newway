package com.rbkmoney.newway.poller.handler.impl.party_mngmnt.party;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyRevisionChanged;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.domain.tables.pojos.Party;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.handler.impl.party_mngmnt.AbstractPartyManagementHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PartyRevisionChangedHandler extends AbstractPartyManagementHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PartyDao partyDao;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Filter filter;

    public PartyRevisionChangedHandler(PartyDao partyDao) {
        this.partyDao = partyDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "revision_changed",
                new IsNullCondition().not()));
    }

    @Override
    public void handle(PartyChange change, Event event) {
        long eventId = event.getId();
        PartyRevisionChanged partyRevisionChanged = change.getRevisionChanged();
        String partyId = event.getSource().getPartyId();
        log.info("Start partySource revision changed handling, eventId={}, partyId={}", eventId, partyId);
        Party partySource = partyDao.get(partyId);
        if (partySource == null) {
            throw new NotFoundException(String.format("Party not found, partyId='%s'", partyId));
        }
        partySource.setId(null);
        partySource.setEventId(eventId);
        partySource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        partySource.setRevision(partyRevisionChanged.getRevision());
        partySource.setRevisionChangedAt(TypeUtil.stringToLocalDateTime(partyRevisionChanged.getTimestamp()));
        partyDao.update(partyId);
        partyDao.save(partySource); //TODO adjustments, payout tools
        log.info("Party revision changed has been saved, eventId={}, partyId={}", eventId, partyId);
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
