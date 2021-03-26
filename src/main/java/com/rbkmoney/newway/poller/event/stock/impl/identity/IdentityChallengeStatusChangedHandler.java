package com.rbkmoney.newway.poller.event.stock.impl.identity;

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
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdentityChallengeStatusChangedHandler implements IdentityHandler {

    private final ChallengeDao challengeDao;
    private final MachineEventCopyFactory<Challenge> challengeMachineEventCopyFactory;

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

        Challenge challengeOld = challengeDao.get(identityId, challengeChange.getId());
        Challenge challengeNew = challengeMachineEventCopyFactory
                .create(event, (int) sequenceId, identityId, timestampedChange.getOccuredAt());

        challengeNew.setChallengeId(challengeChange.getId());
        challengeNew.setChallengeStatus(
                TBaseUtil.unionFieldToEnum(status, com.rbkmoney.newway.domain.enums.ChallengeStatus.class));
        if (status.isSetCompleted()) {
            ChallengeCompleted challengeCompleted = status.getCompleted();
            challengeNew.setChallengeResolution(
                    TypeUtil.toEnumField(challengeCompleted.getResolution().toString(), ChallengeResolution.class));
            if (challengeCompleted.isSetValidUntil()) {
                challengeNew.setChallengeValidUntil(TypeUtil.stringToLocalDateTime(challengeCompleted.getValidUntil()));
            }
        }

        challengeDao.save(challengeNew).ifPresentOrElse(
                id -> {
                    challengeDao.updateNotCurrent(identityId, challengeOld.getId());
                    log.info("Identity challenge status have been changed, sequenceId={}, identityId={}", sequenceId,
                            identityId);
                },
                () -> log.info("Identity challenge have been saved, sequenceId={}, identityId={}", sequenceId,
                        identityId));
    }

}
