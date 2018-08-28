package com.rbkmoney.newway.poller.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.newway.poller.handler.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.poller.handler.impl.party_mngmnt.AbstractPartyManagementHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ProcessingEventStockHandler implements EventHandler<StockEvent> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final List<AbstractInvoicingHandler> invoicingHandlers;
    private final List<AbstractPartyManagementHandler> partyManagementHandlers;

    public ProcessingEventStockHandler(List<AbstractInvoicingHandler> invoicingHandlers, List<AbstractPartyManagementHandler> partyManagementHandlers) {
        this.invoicingHandlers = invoicingHandlers;
        this.partyManagementHandlers = partyManagementHandlers;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public EventAction handle(StockEvent stockEvent, String subsKey) {
        Event processingEvent = stockEvent.getSourceEvent().getProcessingEvent();
        EventPayload payload = processingEvent.getPayload();

        try {
            if (payload.isSetInvoiceChanges()) {
                payload.getInvoiceChanges().forEach(cc -> {
                    invoicingHandlers.forEach(ih -> {
                        if (ih.accept(cc)) {
                            ih.handle(cc, processingEvent);
                        }
                    });
                });
            } else if (payload.isSetPartyChanges()) {
                payload.getPartyChanges().forEach(cc -> partyManagementHandlers.forEach(ph -> {
                    if (ph.accept(cc)) {
                        ph.handle(cc, processingEvent);
                    }
                }));
            }
        } catch (RuntimeException e) {
            log.error("Error when polling processing event with id={}", processingEvent.getId(), e);
            return EventAction.DELAYED_RETRY;
        }
        return EventAction.CONTINUE;
    }

}
