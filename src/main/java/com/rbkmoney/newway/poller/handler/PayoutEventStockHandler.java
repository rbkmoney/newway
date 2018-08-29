package com.rbkmoney.newway.poller.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.damsel.payout_processing.EventPayload;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.newway.poller.handler.impl.payout.AbstractPayoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class PayoutEventStockHandler implements EventHandler<StockEvent> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private List<AbstractPayoutHandler> payoutHandlers;

    @Override
    public EventAction handle(StockEvent stockEvent, String subsKey) {
        Event payoutEvent = stockEvent.getSourceEvent().getPayoutEvent();
        EventPayload payload = payoutEvent.getPayload();
        List<PayoutChange> changes = payload.getPayoutChanges();
        try {
            handleEvents(payoutEvent, changes);
        } catch (RuntimeException e) {
            log.error("Error when polling payout event with id={}", payoutEvent.getId(), e);
            return EventAction.DELAYED_RETRY;
        }
        return EventAction.CONTINUE;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(Event payoutEvent, List<PayoutChange> changes) {
        changes.forEach(c -> payoutHandlers.forEach(ph -> {
            if (ph.accept(c)) {
                ph.handle(c, payoutEvent);
            }
        }));
    }
}
