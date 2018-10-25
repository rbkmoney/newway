package com.rbkmoney.newway.poller.event_stock.impl.wallet;

import com.rbkmoney.fistful.wallet.Account;
import com.rbkmoney.fistful.wallet.Change;
import com.rbkmoney.fistful.wallet.SinkEvent;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.dao.wallet.iface.WalletDao;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import com.rbkmoney.newway.domain.tables.pojos.Wallet;
import com.rbkmoney.newway.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WalletAccountCreatedHandler extends AbstractWalletHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final IdentityDao identityDao;

    private final WalletDao walletDao;

    private final Filter filter;

    @Autowired
    public WalletAccountCreatedHandler(WalletDao walletDao, IdentityDao identityDao) {
        this.walletDao = walletDao;
        this.identityDao = identityDao;
        this.filter = new PathConditionFilter(new PathConditionRule("account.created", new IsNullCondition().not()));
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        Account account = change.getAccount().getCreated();
        log.info("Start wallet account created handling, eventId={}, walletId={}, identityId={}", event.getPayload().getId(), event.getSource(), account.getIdentity());
        Wallet wallet = walletDao.get(event.getSource());
        if (wallet == null) {
            throw new NotFoundException(String.format("Wallet not found, walletId='%s'", event.getSource()));
        }
        Identity identity = identityDao.get(event.getSource());
        if (identity == null) {
            throw new NotFoundException(String.format("Identity not found, walletId='%s'", event.getSource()));
        }

        wallet.setId(null);
        wallet.setWtime(null);
        wallet.setEventId(event.getPayload().getId());
        wallet.setSequenceId(event.getSequence());
        wallet.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        wallet.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        wallet.setIdentityId(account.getIdentity());
        wallet.setPartyId(identity.getPartyId());
        wallet.setCurrencyCode(account.getCurrency().getSymbolicCode());

        walletDao.updateNotCurrent(event.getSource());
        walletDao.save(wallet);
        log.info("Wallet account have been saved, eventId={}, walletId={}, identityId={}", event.getPayload().getId(), event.getSource(), account.getIdentity());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }
}
