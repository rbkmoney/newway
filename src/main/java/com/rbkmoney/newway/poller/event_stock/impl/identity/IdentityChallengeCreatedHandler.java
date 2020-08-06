package com.rbkmoney.newway.poller.event_stock.impl.identity;

import com.rbkmoney.fistful.identity.ChallengeChange;
import com.rbkmoney.fistful.identity.ChallengeChangePayload;
import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.TimestampedChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.identity.iface.ChallengeDao;
import com.rbkmoney.newway.domain.enums.ChallengeStatus;
import com.rbkmoney.newway.domain.tables.pojos.Challenge;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdentityChallengeCreatedHandler extends AbstractIdentityHandler {

    private final ChallengeDao challengeDao;

    @Getter
    private Filter filter = new PathConditionFilter(
            new PathConditionRule("identity_challenge.payload.created", new IsNullCondition().not()));

    @Override
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        ChallengeChange challengeChange = change.getIdentityChallenge();
        long sequenceId = event.getEventId();
        String identityId = event.getSourceId();
        String challengeId = challengeChange.getId();
        log.info("Start identity challenge created handling, sequenceId={}, identityId={}, challengeId={}",
                sequenceId, identityId, challengeId);

        Challenge challenge = new Challenge();
        initDefaultChallengeFields(event, challengeChange, (int) sequenceId, identityId, challenge, timestampedChange.getOccuredAt());

        ChallengeChangePayload challengePayload = challengeChange.getPayload();
        com.rbkmoney.fistful.identity.Challenge challengePayloadCreated = challengePayload.getCreated();
        challenge.setChallengeClassId(challengePayloadCreated.getCls());
        if (challengePayloadCreated.isSetProofs()) {
            challenge.setProofsJson(JsonUtil.objectToJsonString(challengePayloadCreated.getProofs().stream().map(JsonUtil::tBaseToJsonNode).collect(Collectors.toList())));
        }
        challenge.setChallengeStatus(ChallengeStatus.pending);

        challengeDao.save(challenge).ifPresentOrElse(
                id -> log.info("Start identity challenge have been changed, sequenceId={}, identityId={}, challengeId={}",
                        sequenceId, identityId, challengeId),
                () -> log.info("Destination have been saved, sequenceId={}, identityId={}, challengeId={}",
                        sequenceId, identityId, challengeId)
        );
    }

}
