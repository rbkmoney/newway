package com.rbkmoney.newway.poller.event_stock.impl.source;

import com.rbkmoney.fistful.account.Account;
import com.rbkmoney.fistful.source.Change;
import com.rbkmoney.fistful.source.SinkEvent;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.dao.source.iface.SourceDao;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import com.rbkmoney.newway.domain.tables.pojos.Source;
import com.rbkmoney.newway.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SourceAccountCreatedHandler extends AbstractSourceHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SourceDao sourceDao;

    private final IdentityDao identityDao;

    private final Filter filter;

    public SourceAccountCreatedHandler(SourceDao sourceDao, IdentityDao identityDao) {
        this.sourceDao = sourceDao;
        this.identityDao = identityDao;
        this.filter = new PathConditionFilter(new PathConditionRule("account.created", new IsNullCondition().not()));
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        Account account = change.getAccount().getCreated();
        log.info("Start source account created handling, eventId={}, sourceId={}, identityId={}", event.getId(), event.getSource(), account.getIdentity());
        Source source = sourceDao.get(event.getSource());
        if (source == null) {
            throw new NotFoundException(String.format("Source not found, walletId='%s'", event.getSource()));
        }
        Identity identity = identityDao.get(account.getIdentity());
        if (identity == null) {
            throw new NotFoundException(String.format("Identity not found, walletId='%s'", event.getSource()));
        }

        source.setId(null);
        source.setWtime(null);
        source.setEventId(event.getId());
        source.setSequenceId(event.getPayload().getSequence());
        source.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        source.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        source.setAccountId(account.getId());
        source.setIdentityId(account.getIdentity());
        source.setPartyId(identity.getPartyId());
        source.setAccounterAccountId(account.getAccounterAccountId());
        source.setCurrencyCode(account.getCurrency().getSymbolicCode());

        sourceDao.updateNotCurrent(event.getSource());
        sourceDao.save(source);
        log.info("Source account have been saved, eventId={}, sourceId={}, identityId={}", event.getId(), event.getSource(), account.getIdentity());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }
}
