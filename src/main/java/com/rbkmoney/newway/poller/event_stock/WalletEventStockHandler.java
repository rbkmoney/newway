package com.rbkmoney.newway.poller.event_stock;

import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.fistful.wallet.Event;
import com.rbkmoney.fistful.wallet.SinkEvent;
import com.rbkmoney.newway.poller.event_stock.impl.wallet.AbstractWalletHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class WalletEventStockHandler implements EventHandler<SinkEvent> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final List<AbstractWalletHandler> walletHandlers;

    public WalletEventStockHandler(List<AbstractWalletHandler> walletHandlers) {
        this.walletHandlers = walletHandlers;
    }

    @Override
    public EventAction handle(SinkEvent sinkEvent, String subsKey) {
        try {
            handleEvents(sinkEvent, sinkEvent.getPayload());
        } catch (RuntimeException e) {
            log.error("Error when polling wallet event with id={}", sinkEvent.getPayload().getId(), e);
            return EventAction.DELAYED_RETRY;
        }
        return EventAction.CONTINUE;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(SinkEvent sinkEvent, Event payload) {
        payload.getChanges().forEach(cc -> walletHandlers.forEach(ph -> {
            if (ph.accept(cc)) {
                ph.handle(cc, sinkEvent);
            }
        }));
    }

}
