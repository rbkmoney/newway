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
import com.rbkmoney.newway.domain.enums.ChallengeStatus;
import com.rbkmoney.newway.domain.tables.pojos.Challenge;
import com.rbkmoney.newway.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

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
        log.info("Start identity challenge created handling, eventId={}, identityId={}, challengeId={}", event.getId(), event.getSource(), challengeChange.getId());

        Challenge challenge = new Challenge();
        challenge.setEventId(event.getId());
        challenge.setSequenceId(event.getPayload().getSequence());
        challenge.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        challenge.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        challenge.setIdentityId(event.getSource());
        challenge.setChallengeId(challengeChange.getId());

        ChallengeChangePayload challengePayload = challengeChange.getPayload();
        com.rbkmoney.fistful.identity.Challenge challengePayloadCreated = challengePayload.getCreated();
        challenge.setChallengeClassId(challengePayloadCreated.getCls());
        if (challengePayloadCreated.isSetProofs()) {
            challenge.setProofsJson(JsonUtil.objectToJsonString(challengePayloadCreated.getProofs().stream().map(JsonUtil::tBaseToJsonNode).collect(Collectors.toList())));
        }
        challenge.setChallengeStatus(ChallengeStatus.pending);

        challengeDao.updateNotCurrent(event.getSource(), challengeChange.getId());
        challengeDao.save(challenge);
        log.info("Start identity challenge have been created, eventId={}, identityId={}, challengeId={}", event.getId(), event.getSource(), challengeChange.getId());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }
}
