package com.rbkmoney.newway.poller.event_stock.impl.identity;

import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IdentityEffectiveChallengeChangedHandler extends AbstractIdentityHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final IdentityDao identityDao;

    private Filter filter;

    public IdentityEffectiveChallengeChangedHandler(IdentityDao identityDao) {
        this.identityDao = identityDao;
        this.filter = new PathConditionFilter(new PathConditionRule("effective_challenge_changed", new IsNullCondition().not()));
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        log.info("Start effective identity challenge changed handling, eventId={}, walletId={}, effectiveChallengeId={}", event.getPayload().getId(), event.getSource(), change.getEffectiveChallengeChanged());
        Identity identity = identityDao.get(event.getSource());

        identity.setId(null);
        identity.setWtime(null);
        identity.setEventId(event.getPayload().getId());
        identity.setSequenceId(event.getSequence());
        identity.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        identity.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        identity.setIdentityId(event.getSource());
        identity.setIdentityEffectiveChalengeId(change.getEffectiveChallengeChanged());

        identityDao.updateNotCurrent(event.getSource());
        identityDao.save(identity);
        log.info("Effective identity challenge have been changed, eventId={}, walletId={}, effectiveChallengeId={}", event.getPayload().getId(), event.getSource(), change.getEffectiveChallengeChanged());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }

}
