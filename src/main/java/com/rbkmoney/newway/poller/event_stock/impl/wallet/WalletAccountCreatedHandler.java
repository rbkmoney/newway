package com.rbkmoney.newway.poller.event_stock.impl.wallet;

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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletAccountCreatedHandler extends AbstractWalletHandler {

    private final IdentityDao identityDao;
    private final WalletDao walletDao;

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
        Wallet wallet = walletDao.get(walletId);
        if (wallet == null) {
            throw new NotFoundException(String.format("Wallet not found, walletId='%s'", walletId));
        }
        Long oldId = wallet.getId();
        Identity identity = identityDao.get(account.getIdentity());
        if (identity == null) {
            throw new NotFoundException(String.format("Identity not found, walletId='%s'", walletId));
        }

        initDefaultFields(event, sequenceId, walletId, wallet, timestampedChange.getOccuredAt());

        wallet.setAccountId(account.getId());
        wallet.setIdentityId(account.getIdentity());
        wallet.setPartyId(identity.getPartyId());
        wallet.setAccounterAccountId(account.getAccounterAccountId());
        wallet.setCurrencyCode(account.getCurrency().getSymbolicCode());

        walletDao.save(wallet).ifPresentOrElse(
                id -> {
                    walletDao.updateNotCurrent(oldId);
                    log.info("Wallet account have been changed, sequenceId={}, walletId={}", sequenceId, walletId);
                },
                () -> log.info("Wallet account have been saved, sequenceId={}, walletId={}", sequenceId, walletId));
    }

}
