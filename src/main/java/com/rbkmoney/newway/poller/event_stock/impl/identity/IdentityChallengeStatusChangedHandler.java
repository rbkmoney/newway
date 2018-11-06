package com.rbkmoney.newway.poller.event_stock.impl.identity;

import com.rbkmoney.fistful.identity.ChallengeChange;
import com.rbkmoney.fistful.identity.ChallengeCompleted;
import com.rbkmoney.fistful.identity.ChallengeStatus;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.identity.iface.ChallengeDao;
import com.rbkmoney.newway.domain.enums.ChallengeResolution;
import com.rbkmoney.newway.domain.tables.pojos.Challenge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class IdentityChallengeStatusChangedHandler extends AbstractIdentityHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ChallengeDao challengeDao;

    private Filter filter;

    public IdentityChallengeStatusChangedHandler(ChallengeDao challengeDao) {
        this.challengeDao = challengeDao;
        this.filter = new PathConditionFilter(new PathConditionRule("identity_challenge.payload.status_changed", new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(Change change, SinkEvent event) {
        ChallengeChange challengeChange = change.getIdentityChallenge();
        ChallengeStatus status = challengeChange.getPayload().getStatusChanged();
        log.info("Start identity challenge status changed handling, eventId={}, walletId={}, challengeId={}, status={}", event.getId(), event.getSource(), challengeChange.getId(), status);

        Challenge challenge = challengeDao.get(event.getSource(), challengeChange.getId());

        challenge.setId(null);
        challenge.setWtime(null);
        challenge.setEventId(event.getId());
        challenge.setSequenceId(event.getPayload().getSequence());
        challenge.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        challenge.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        challenge.setIdentityId(event.getSource());
        challenge.setChallengeId(challengeChange.getId());

        challenge.setChallengeStatus(TBaseUtil.unionFieldToEnum(status, com.rbkmoney.newway.domain.enums.ChallengeStatus.class));
        if (status.isSetCompleted()) {
            ChallengeCompleted challengeCompleted = status.getCompleted();
            challenge.setChallengeResolution(TypeUtil.toEnumField(challengeCompleted.getResolution().toString(), ChallengeResolution.class));
            if (challengeCompleted.isSetValidUntil()) {
                challenge.setChallengeValidUntil(TypeUtil.stringToLocalDateTime(challengeCompleted.getValidUntil()));
            }
        }

        challengeDao.updateNotCurrent(event.getSource(), challengeChange.getId());
        challengeDao.save(challenge);
        log.info("Identity challenge status have been changed, eventId={}, walletId={}, challengeId={}, status={}", event.getId(), event.getSource(), challengeChange.getId(), status);
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }

}
