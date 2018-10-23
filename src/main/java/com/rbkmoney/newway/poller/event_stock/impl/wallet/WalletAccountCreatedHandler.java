package com.rbkmoney.newway.poller.event_stock.impl.wallet;

import com.rbkmoney.fistful.wallet.Change;
import com.rbkmoney.fistful.wallet.SinkEvent;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.wallet.iface.WalletDao;
import com.rbkmoney.newway.domain.tables.pojos.Wallet;
import com.rbkmoney.newway.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WalletAccountCreatedHandler extends AbstractWalletHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WalletDao walletDao;

    private final Filter filter;

    @Autowired
    public WalletAccountCreatedHandler(WalletDao walletDao) {
        this.walletDao = walletDao;
        this.filter = new PathConditionFilter(new PathConditionRule("account.created", new IsNullCondition().not()));
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        Wallet wallet = walletDao.get(event.getSource());
        if (wallet == null) {
            throw new NotFoundException(String.format("Wallet not found, walletId='%s'", event.getSource()));
        }

        wallet.setId(null);
        wallet.setWtime(null);
        wallet.setEventId(event.getPayload().getId());
        wallet.setSequenceId(event.getSequence());
        wallet.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        wallet.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        wallet.setIdentityId(change.getAccount().getCreated().getIdentity());
        wallet.setIdentityId(change.getAccount().getCreated().getCurrency().getSymbolicCode());

        walletDao.updateNotCurrent(event.getSource());
        walletDao.save(wallet);
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }
}
