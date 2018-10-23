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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WalletCreatedHandler extends AbstractWalletHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WalletDao walletDao;

    private final Filter filter;

    public WalletCreatedHandler(WalletDao walletDao) {
        this.walletDao = walletDao;
        this.filter = new PathConditionFilter(new PathConditionRule("created", new IsNullCondition().not()));
    }

    @Override
    public void handle(Change change, SinkEvent event) {
//        log.info("Start invoice created handling, eventId={}, invoiceId={}, partyId={}, shopId={}",
//                eventId, invoice.getId(), invoice.getOwnerId(), invoice.getShopId());
        Wallet wallet = new Wallet();
        wallet.setEventId(event.getPayload().getId());
        wallet.setSequenceId(event.getSequence());
        wallet.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        wallet.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        wallet.setWalletId(event.getSource());
        wallet.setWalletName(change.getCreated().getName());

        walletDao.save(wallet);
//        log.info("Invoice has been saved, eventId={}, invoiceId={}, partyId={}, shopId={}",
//                eventId, invoice.getId(), invoice.getOwnerId(), invoice.getShopId());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }

}
