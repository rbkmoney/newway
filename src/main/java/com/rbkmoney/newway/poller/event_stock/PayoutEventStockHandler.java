package com.rbkmoney.newway.poller.event_stock;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.damsel.payout_processing.EventPayload;
import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.newway.service.PayoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PayoutEventStockHandler implements EventHandler<StockEvent> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PayoutService payoutService;

    public PayoutEventStockHandler(PayoutService payoutService) {
        this.payoutService = payoutService;
    }

    @Override
    public EventAction handle(StockEvent stockEvent, String subsKey) {
        Event payoutEvent = stockEvent.getSourceEvent().getPayoutEvent();
        EventPayload payload = payoutEvent.getPayload();
        try {
            payoutService.handleEvents(payoutEvent, payload);
        } catch (RuntimeException e) {
            log.error("Error when polling payout event with id={}", payoutEvent.getId(), e);
            return EventAction.DELAYED_RETRY;
        }
        return EventAction.CONTINUE;
    }
}
