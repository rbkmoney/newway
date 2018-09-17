package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.party;

import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyMetaSet;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.domain.tables.pojos.Party;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractPartyManagementHandler;
import com.rbkmoney.newway.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PartyMetaSetHandler extends AbstractPartyManagementHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PartyDao partyDao;

    private final Filter filter;

    public PartyMetaSetHandler(PartyDao partyDao) {
        this.partyDao = partyDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "party_meta_set",
                new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, Event event) {
        long eventId = event.getId();
        PartyMetaSet partyMetaSet = change.getPartyMetaSet();
        String partyId = event.getSource().getPartyId();
        log.info("Start party metaset handling, eventId={}, partyId={}", eventId, partyId);
        Party partySource = partyDao.get(partyId);
        if (partySource == null) {
            throw new NotFoundException(String.format("Party not found, partyId='%s'", partyId));
        }
        partySource.setId(null);
        partySource.setWtime(null);
        partySource.setEventId(eventId);
        partySource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        partySource.setPartyMetaSetNs(partyMetaSet.getNs());
        partySource.setPartyMetaSetDataJson(JsonUtil.toJsonString(partyMetaSet.getData()));
        partyDao.updateNotCurrent(partyId);
        partyDao.save(partySource);
        log.info("Party metaset has been saved, eventId={}, partyId={}", eventId, partyId);
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
