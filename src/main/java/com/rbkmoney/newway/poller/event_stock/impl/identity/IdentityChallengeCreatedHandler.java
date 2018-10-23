package com.rbkmoney.newway.poller.event_stock.impl.identity;

import com.rbkmoney.fistful.identity.ChallengeChange;
import com.rbkmoney.fistful.identity.ChallengeChangePayload;
import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.identity.iface.ChallengeDao;
import com.rbkmoney.newway.domain.tables.pojos.Challenge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IdentityChallengeCreatedHandler extends AbstractIdentityHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ChallengeDao challengeDao;

    private Filter filter;

    public IdentityChallengeCreatedHandler(ChallengeDao challengeDao) {
        this.challengeDao = challengeDao;
        this.filter = new PathConditionFilter(new PathConditionRule("identity_challenge.payload.created", new IsNullCondition().not()));
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        ChallengeChange challengeChange = change.getIdentityChallenge();

        Challenge challenge = new Challenge();
        challenge.setEventId(event.getPayload().getId());
        challenge.setSequenceId(event.getSequence());
        challenge.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        challenge.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        challenge.setIdentityId(event.getSource());
        challenge.setChallengeId(challengeChange.getId());

        ChallengeChangePayload challengePayload = challengeChange.getPayload();
        challenge.setChallengeClassId(challengePayload.getCreated().getCls());

        challengeDao.save(challenge);
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }
}
