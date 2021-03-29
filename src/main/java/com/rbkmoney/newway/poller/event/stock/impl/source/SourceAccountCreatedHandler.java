package com.rbkmoney.newway.poller.event.stock.impl.source;

import com.rbkmoney.fistful.account.Account;
import com.rbkmoney.fistful.source.Change;
import com.rbkmoney.fistful.source.TimestampedChange;
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
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SourceAccountCreatedHandler implements SourceHandler {

    private final SourceDao sourceDao;
    private final IdentityDao identityDao;
    private final MachineEventCopyFactory<Source, String> sourceMachineEventCopyFactory;

    @Getter
    private final Filter filter =
            new PathConditionFilter(new PathConditionRule("change.account.created", new IsNullCondition().not()));

    @Override
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        Account account = change.getAccount().getCreated();
        long sequenceId = event.getEventId();
        String sourceId = event.getSourceId();
        log.info("Start source account created handling, sequenceId={}, sourceId={}", sequenceId, sourceId);
        final Source sourceOld = sourceDao.get(sourceId);
        if (sourceOld == null) {
            throw new NotFoundException(String.format("Source not found, sourceId='%s'", sourceId));
        }
        Identity identity = identityDao.get(account.getIdentity());
        if (identity == null) {
            throw new NotFoundException(String.format("Identity not found, sourceId='%s'", account.getIdentity()));
        }

        Source sourceNew = sourceMachineEventCopyFactory
                .create(event, sequenceId, sourceId, sourceOld, timestampedChange.getOccuredAt());

        sourceNew.setAccountId(account.getId());
        sourceNew.setIdentityId(account.getIdentity());
        sourceNew.setPartyId(identity.getPartyId());
        sourceNew.setAccounterAccountId(account.getAccounterAccountId());
        sourceNew.setCurrencyCode(account.getCurrency().getSymbolicCode());

        sourceDao.save(sourceOld).ifPresentOrElse(
                id -> {
                    sourceDao.updateNotCurrent(sourceOld.getId());
                    log.info("Source account have been changed, sequenceId={}, sourceId={}", sequenceId, sourceId);
                },
                () -> log.info("Source account have been saved, sequenceId={}, sourceId={}", sequenceId, sourceId));
    }


}
