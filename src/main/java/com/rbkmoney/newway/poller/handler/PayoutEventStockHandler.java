package com.rbkmoney.newway.poller.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.damsel.payout_processing.EventPayload;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.newway.poller.handler.impl.payout.AbstractPayoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PayoutEventStockHandler implements EventHandler<StockEvent> {

    @Autowired
    private List<AbstractPayoutHandler> payoutHandlers;

    @Override
    public EventAction handle(StockEvent stockEvent, String subsKey) {
        Event payoutEvent = stockEvent.getSourceEvent().getPayoutEvent();
        EventPayload payload = payoutEvent.getPayload();
        List<PayoutChange> changes = payload.getPayoutChanges();
        changes.forEach(c -> payoutHandlers.forEach(ph -> {
            if (ph.accept(c)) {
                ph.handle(c, payoutEvent);
            }
        }));
        return EventAction.CONTINUE;
    }

}
