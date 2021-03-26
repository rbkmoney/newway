package com.rbkmoney.newway.poller.event.stock.impl.destination;

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
import com.rbkmoney.newway.exception.NotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DestinationAccountCreatedHandler extends AbstractDestinationHandler {

    private final DestinationDao destinationDao;
    private final IdentityDao identityDao;

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
        Destination destination = destinationDao.get(destinationId);
        if (destination == null) {
            throw new NotFoundException(String.format("Destination not found, destinationId='%s'", destinationId));
        }
        Identity identity = identityDao.get(account.getIdentity());

        if (identity == null) {
            throw new NotFoundException(String.format("Identity not found, identityId='%s'", account.getIdentity()));
        }
        Long oldId = destination.getId();
        initDefaultFields(event, sequenceId, destinationId, destination, timestampedChange.getOccuredAt());
        destination.setAccountId(account.getId());
        destination.setIdentityId(account.getIdentity());
        destination.setPartyId(identity.getPartyId());
        destination.setAccounterAccountId(account.getAccounterAccountId());
        destination.setCurrencyCode(account.getCurrency().getSymbolicCode());

        destinationDao.save(destination).ifPresentOrElse(
                id -> {
                    destinationDao.updateNotCurrent(oldId);
                    log.info("Destination account have been changed, sequenceId={}, destinationId={}", sequenceId,
                            destinationId);
                },
                () -> log
                        .info("Destination have been saved, sequenceId={}, destinationId={}", sequenceId, destinationId)
        );
    }


}
