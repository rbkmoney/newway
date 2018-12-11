package com.rbkmoney.newway.poller.event_stock;

import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.newway.service.RateService;
import com.rbkmoney.xrates.rate.SinkEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RateEventStockHandler implements EventHandler<SinkEvent> {

    private final RateService rateService;

    @Override
    public EventAction handle(SinkEvent sinkEvent, String subsKey) {
        try {
            rateService.handleEvents(sinkEvent, sinkEvent.getPayload());
        } catch (RuntimeException e) {
            log.error("Error when polling rates event with id={}", sinkEvent.getId(), e);
            return EventAction.DELAYED_RETRY;
        }
        return EventAction.CONTINUE;
    }
}
