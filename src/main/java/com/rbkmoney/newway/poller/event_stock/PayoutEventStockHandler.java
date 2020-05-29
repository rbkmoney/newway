package com.rbkmoney.newway.poller.event_stock;

import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.newway.service.PayoutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PayoutEventStockHandler implements EventHandler<Event> {

    private final PayoutService payoutService;

    public PayoutEventStockHandler(PayoutService payoutService) {
        this.payoutService = payoutService;
    }

    @Override
    public EventAction handle(Event event, String subsKey) {
        try {
            payoutService.handleEvents(event, event.getPayload());
        } catch (RuntimeException e) {
            log.error("Error when polling payout event with id={}", event.getId(), e);
            return EventAction.DELAYED_RETRY;
        }
        return EventAction.CONTINUE;
    }

}
