package com.rbkmoney.newway.poller.event.stock.impl.identity;

import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.TimestampedChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
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
public class IdentityEffectiveChallengeChangedHandler implements IdentityHandler {

    private final IdentityDao identityDao;
    private final MachineEventCopyFactory<Identity, String> identityMachineEventCopyFactory;

    @Getter
    private Filter filter = new PathConditionFilter(
            new PathConditionRule("change.effective_challenge_changed", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        long sequenceId = event.getEventId();
        String identityId = event.getSourceId();
        log.info("Start effective identity challenge changed handling, sequenceId={}, identityId={}", sequenceId,
                identityId);
        final Identity identityOld = identityDao.get(identityId);
        Identity identityNew = identityMachineEventCopyFactory
                .create(event, sequenceId, identityId, identityOld, timestampedChange.getOccuredAt());

        identityNew.setIdentityEffectiveChalengeId(change.getEffectiveChallengeChanged());

        identityDao.save(identityNew).ifPresentOrElse(
                id -> {
                    identityDao.updateNotCurrent(identityOld.getId());
                    log.info("Effective identity challenge have been changed, sequenceId={}, identityId={}", sequenceId,
                            identityId);
                },
                () -> log.info("Effective identity challenge have been saved, sequenceId={}, identityId={}", sequenceId,
                        identityId));
    }

}
