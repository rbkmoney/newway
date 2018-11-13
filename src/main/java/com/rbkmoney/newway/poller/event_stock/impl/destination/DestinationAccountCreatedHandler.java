package com.rbkmoney.newway.poller.event_stock.impl.destination;

import com.rbkmoney.fistful.account.Account;
import com.rbkmoney.fistful.destination.Change;
import com.rbkmoney.fistful.destination.SinkEvent;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.destination.iface.DestinationDao;
import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.domain.tables.pojos.Destination;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import com.rbkmoney.newway.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DestinationAccountCreatedHandler extends AbstractDestinationHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final DestinationDao destinationDao;

    private final IdentityDao identityDao;

    private final Filter filter;

    public DestinationAccountCreatedHandler(DestinationDao destinationDao, IdentityDao identityDao) {
        this.destinationDao = destinationDao;
        this.identityDao = identityDao;
        this.filter = new PathConditionFilter(new PathConditionRule("account.created", new IsNullCondition().not()));
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        Account account = change.getAccount().getCreated();
        log.info("Start destination account created handling, eventId={}, destinationId={}, identityId={}", event.getId(), event.getSource(), account.getIdentity());
        Destination destination = destinationDao.get(event.getSource());
        if (destination == null) {
            throw new NotFoundException(String.format("Destination not found, walletId='%s'", event.getSource()));
        }
        Identity identity = identityDao.get(account.getIdentity());
        if (identity == null) {
            throw new NotFoundException(String.format("Identity not found, walletId='%s'", event.getSource()));
        }

        destination.setId(null);
        destination.setWtime(null);
        destination.setEventId(event.getId());
        destination.setSequenceId(event.getPayload().getSequence());
        destination.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        destination.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        destination.setAccountId(account.getId());
        destination.setIdentityId(account.getIdentity());
        destination.setPartyId(identity.getPartyId());
        destination.setAccounterAccountId(account.getAccounterAccountId());
        destination.setCurrencyCode(account.getCurrency().getSymbolicCode());

        destinationDao.updateNotCurrent(event.getSource());
        destinationDao.save(destination);
        log.info("Destination account have been saved, eventId={}, destinationId={}, identityId={}", event.getId(), event.getSource(), account.getIdentity());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }
}
