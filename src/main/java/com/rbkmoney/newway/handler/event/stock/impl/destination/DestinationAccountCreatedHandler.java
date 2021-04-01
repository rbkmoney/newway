package com.rbkmoney.newway.handler.event.stock.impl.destination;

import com.rbkmoney.fistful.account.Account;
import com.rbkmoney.fistful.destination.Change;
import com.rbkmoney.fistful.destination.TimestampedChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.destination.iface.DestinationDao;
import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.domain.tables.pojos.Destination;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DestinationAccountCreatedHandler implements DestinationHandler {

    private final DestinationDao destinationDao;
    private final IdentityDao identityDao;
    private final MachineEventCopyFactory<Destination, String> destinationMachineEventCopyFactory;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.account.created", new IsNullCondition().not()));

    @Override
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        Account account = change.getAccount().getCreated();
        long sequenceId = event.getEventId();
        String destinationId = event.getSourceId();
        log.info("Start destination account created handling, sequenceId={}, destinationId={}", sequenceId,
                destinationId);
        final Destination destinationOld = destinationDao.get(destinationId);
        Identity identity = identityDao.get(account.getIdentity());
        Destination destinationNew = destinationMachineEventCopyFactory
                .create(event, sequenceId, destinationId, destinationOld, timestampedChange.getOccuredAt());

        destinationNew.setAccountId(account.getId());
        destinationNew.setIdentityId(account.getIdentity());
        destinationNew.setPartyId(identity.getPartyId());
        destinationNew.setAccounterAccountId(account.getAccounterAccountId());
        destinationNew.setCurrencyCode(account.getCurrency().getSymbolicCode());

        destinationDao.save(destinationNew).ifPresentOrElse(
                id -> {
                    destinationDao.updateNotCurrent(destinationOld.getId());
                    log.info("Destination account have been changed, sequenceId={}, destinationId={}", sequenceId,
                            destinationId);
                },
                () -> log
                        .info("Destination have been saved, sequenceId={}, destinationId={}", sequenceId, destinationId)
        );
    }

}
