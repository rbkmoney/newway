package com.rbkmoney.newway.poller.event_stock.impl.source;

import com.rbkmoney.fistful.account.Account;
import com.rbkmoney.fistful.source.Change;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.dao.source.iface.SourceDao;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import com.rbkmoney.newway.domain.tables.pojos.Source;
import com.rbkmoney.newway.exception.NotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SourceAccountCreatedHandler extends AbstractSourceHandler {

    private final SourceDao sourceDao;
    private final IdentityDao identityDao;

    @Getter
    private final Filter filter = new PathConditionFilter(new PathConditionRule("account.created", new IsNullCondition().not()));

    @Override
    public void handle(Change change, MachineEvent event) {
        Account account = change.getAccount().getCreated();
        long sequenceId = event.getEventId();
        String sourceId = event.getSourceId();
        log.info("Start source account created handling, sequenceId={}, sourceId={}", sequenceId, sourceId);
        Source source = sourceDao.get(sourceId);
        if (source == null) {
            throw new NotFoundException(String.format("Source not found, walletId='%s'", sourceId));
        }
        Long oldId = source.getId();
        Identity identity = identityDao.get(account.getIdentity());
        if (identity == null) {
            throw new NotFoundException(String.format("Identity not found, walletId='%s'", sourceId));
        }

        initDefaultFields(event, (int) sequenceId, sourceId, source);

        source.setAccountId(account.getId());
        source.setIdentityId(account.getIdentity());
        source.setPartyId(identity.getPartyId());
        source.setAccounterAccountId(account.getAccounterAccountId());
        source.setCurrencyCode(account.getCurrency().getSymbolicCode());

        sourceDao.save(source).ifPresentOrElse(
                id -> {
                    sourceDao.updateNotCurrent(oldId);
                    log.info("Source account have been changed, sequenceId={}, sourceId={}", sequenceId, sourceId);
                },
                () -> log.info("Source account have been saved, sequenceId={}, sourceId={}", sequenceId, sourceId));
    }


}
