package com.rbkmoney.newway.poller.event_stock;

import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.fistful.deposit.SinkEvent;
import com.rbkmoney.newway.service.DepositService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DepositEventStockHandler implements EventHandler<SinkEvent> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final DepositService depositService;

    public DepositEventStockHandler(DepositService depositService) {
        this.depositService = depositService;
    }

    @Override
    public EventAction handle(SinkEvent sinkEvent, String subsKey) {
        try {
            depositService.handleEvents(sinkEvent, sinkEvent.getPayload());
        } catch (RuntimeException e) {
            log.error("Error when polling deposit event with id={}", sinkEvent.getId(), e);
            return EventAction.DELAYED_RETRY;
        }
        return EventAction.CONTINUE;
    }

}
