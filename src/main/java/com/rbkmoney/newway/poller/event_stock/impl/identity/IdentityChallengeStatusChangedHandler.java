package com.rbkmoney.newway.poller.event_stock.impl.identity;

import com.rbkmoney.fistful.identity.*;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.identity.iface.ChallengeDao;
import com.rbkmoney.newway.domain.enums.ChallengeResolution;
import com.rbkmoney.newway.domain.tables.pojos.Challenge;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdentityChallengeStatusChangedHandler extends AbstractIdentityHandler {

    private final ChallengeDao challengeDao;

    @Getter
    private Filter filter = new PathConditionFilter(
            new PathConditionRule("change.identity_challenge.payload.status_changed", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        ChallengeChange challengeChange = change.getIdentityChallenge();
        ChallengeStatus status = challengeChange.getPayload().getStatusChanged();
        long sequenceId = event.getEventId();
        String identityId = event.getSourceId();
        String challengeId = challengeChange.getId();
        log.info("Start identity challenge status changed handling, sequenceId={}, identityId={}, challengeId={}",
                sequenceId, identityId, challengeId);

        Challenge challenge = challengeDao.get(identityId, challengeChange.getId());
        Long oldId = challenge.getId();

        initDefaultChallengeFields(event, challengeChange, (int) sequenceId, identityId, challenge, timestampedChange.getOccuredAt());

        challenge.setChallengeStatus(TBaseUtil.unionFieldToEnum(status, com.rbkmoney.newway.domain.enums.ChallengeStatus.class));
        if (status.isSetCompleted()) {
            ChallengeCompleted challengeCompleted = status.getCompleted();
            challenge.setChallengeResolution(TypeUtil.toEnumField(challengeCompleted.getResolution().toString(), ChallengeResolution.class));
            if (challengeCompleted.isSetValidUntil()) {
                challenge.setChallengeValidUntil(TypeUtil.stringToLocalDateTime(challengeCompleted.getValidUntil()));
            }
        }

        challengeDao.save(challenge).ifPresentOrElse(
                id -> {
                    challengeDao.updateNotCurrent(identityId, oldId);
                    log.info("Identity challenge status have been changed, sequenceId={}, identityId={}", sequenceId, identityId);
                },
                () -> log.info("Destination have been saved, sequenceId={}, identityId={}", sequenceId, identityId));
    }

}
