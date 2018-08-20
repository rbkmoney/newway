package com.rbkmoney.newway.poller.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.newway.poller.handler.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.poller.handler.impl.party_mngmnt.AbstractPartyManagementHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProcessingEventStockHandler implements EventHandler<StockEvent> {

    private final List<AbstractInvoicingHandler> invoicingHandlers;
    private final List<AbstractPartyManagementHandler> partyManagementHandlers;

    public ProcessingEventStockHandler(List<AbstractInvoicingHandler> invoicingHandlers, List<AbstractPartyManagementHandler> partyManagementHandlers) {
        this.invoicingHandlers = invoicingHandlers;
        this.partyManagementHandlers = partyManagementHandlers;
    }

    @Override
    public EventAction handle(StockEvent stockEvent, String subsKey) {
        EventPayload payload = stockEvent.getSourceEvent().getProcessingEvent().getPayload();

        if (payload.isSetInvoiceChanges()) {
            payload.getInvoiceChanges().forEach(cc -> {
                invoicingHandlers.forEach(ih -> {
                    if (ih.accept(cc)) {
                        ih.handle(cc, stockEvent.getSourceEvent().getProcessingEvent());
                    }
                });
            });
        } else if (payload.isSetPartyChanges()) {
            payload.getPartyChanges().forEach(cc -> partyManagementHandlers.forEach(ph -> {
                if (ph.accept(cc)) {
                    ph.handle(cc, stockEvent.getSourceEvent().getProcessingEvent());
                }
            }));
        }
        return EventAction.CONTINUE;
    }

}
