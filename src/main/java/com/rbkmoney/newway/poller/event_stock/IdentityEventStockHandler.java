package com.rbkmoney.newway.poller.event_stock;

import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.fistful.identity.Event;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.newway.poller.event_stock.impl.identity.AbstractIdentityHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class IdentityEventStockHandler implements EventHandler<SinkEvent> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final List<AbstractIdentityHandler> identityHandlers;

    public IdentityEventStockHandler(List<AbstractIdentityHandler> identityHandlers) {
        this.identityHandlers = identityHandlers;
    }

    @Override
    public EventAction handle(SinkEvent sinkEvent, String subsKey) {
        try {
            handleEvents(sinkEvent, sinkEvent.getPayload());
        } catch (RuntimeException e) {
            log.error("Error when polling identity event with id={}", sinkEvent.getId(), e);
            return EventAction.DELAYED_RETRY;
        }
        return EventAction.CONTINUE;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(SinkEvent sinkEvent, Event payload) {
        payload.getChanges().forEach(cc -> identityHandlers.forEach(ph -> {
                if (ph.accept(cc)) {
                    ph.handle(cc, sinkEvent);
                }
            }));
    }

}
