package com.rbkmoney.newway.poller.event.stock.impl.wallet;

import com.rbkmoney.fistful.account.Account;
import com.rbkmoney.fistful.wallet.Change;
import com.rbkmoney.fistful.wallet.TimestampedChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.dao.wallet.iface.WalletDao;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import com.rbkmoney.newway.domain.tables.pojos.Wallet;
import com.rbkmoney.newway.exception.NotFoundException;
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
public class WalletAccountCreatedHandler implements WalletHandler {

    private final IdentityDao identityDao;
    private final WalletDao walletDao;
    private final MachineEventCopyFactory<Wallet> walletMachineEventCopyFactory;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.account.created", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        Account account = change.getAccount().getCreated();
        long sequenceId = event.getEventId();
        String walletId = event.getSourceId();
        log.info("Start wallet account created handling, sequenceId={}, walletId={}",
                sequenceId, walletId);

        final Wallet walletOld = walletDao.get(walletId);
        Identity identity = findIdentity(account, walletId, walletOld);
        Wallet walletNew = walletMachineEventCopyFactory
                .create(event, sequenceId, walletId, walletOld, timestampedChange.getOccuredAt());
        walletNew.setIdentityId(account.getIdentity());
        walletNew.setAccountId(account.getId());
        walletNew.setPartyId(identity.getPartyId());
        walletNew.setAccounterAccountId(account.getAccounterAccountId());
        walletNew.setCurrencyCode(account.getCurrency().getSymbolicCode());

        walletDao.save(walletNew).ifPresentOrElse(
                id -> {
                    walletDao.updateNotCurrent(walletOld.getId());
                    log.info("Wallet account have been changed, sequenceId={}, walletId={}", sequenceId, walletId);
                },
                () -> log.info("Wallet account have been saved, sequenceId={}, walletId={}", sequenceId, walletId));
    }

    private Identity findIdentity(Account account, String walletId, Wallet walletOld) {
        if (walletOld == null) {
            throw new NotFoundException(String.format("Wallet not found, walletId='%s'", walletId));
        }
        Identity identity = identityDao.get(account.getIdentity());
        if (identity == null) {
            throw new NotFoundException(String.format("Identity not found, walletId='%s'", walletId));
        }
        return identity;
    }

}
