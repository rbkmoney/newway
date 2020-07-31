package com.rbkmoney.newway.poller.event_stock.impl.identity;

import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdentityEffectiveChallengeChangedHandler extends AbstractIdentityHandler {

    private final IdentityDao identityDao;

    @Getter
    private Filter filter = new PathConditionFilter(
            new PathConditionRule("effective_challenge_changed", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(Change change, MachineEvent event) {
        long sequenceId = event.getEventId();
        String identityId = event.getSourceId();
        log.info("Start effective identity challenge changed handling, sequenceId={}, identityId={}", sequenceId, identityId);
        Identity identity = identityDao.get(identityId);

        Long oldId = identity.getId();

        initDefaultFieldsIdentity(change, event, sequenceId, identityId, identity);

        identityDao.save(identity).ifPresentOrElse(
                id -> {
                    identityDao.updateNotCurrent(oldId);
                    log.info("Effective identity challenge have been changed, sequenceId={}, identityId={}", sequenceId, identityId);
                },
                () -> log.info("Destination have been saved, sequenceId={}, identityId={}", sequenceId, identityId));
    }

}
