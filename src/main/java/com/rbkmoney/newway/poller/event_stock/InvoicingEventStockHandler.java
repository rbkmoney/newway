package com.rbkmoney.newway.poller.event_stock;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.newway.service.InvoicingService;
import com.rbkmoney.newway.util.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvoicingEventStockHandler implements EventHandler<StockEvent> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final InvoicingService invoicingService;

    private final int divider;
    private final int mod;

    public InvoicingEventStockHandler(InvoicingService invoicingService, int divider, int mod) {
        this.invoicingService = invoicingService;
        this.divider = divider;
        this.mod = mod;
    }

    public int getDivider() {
        return divider;
    }

    public int getMod() {
        return mod;
    }

    @Override
    public EventAction handle(StockEvent stockEvent, String subsKey) {
        Event processingEvent = stockEvent.getSourceEvent().getProcessingEvent();
        EventPayload payload = processingEvent.getPayload();
        if (payload.isSetInvoiceChanges()) {
            if (HashUtil.checkHashMod(processingEvent.getSource().getInvoiceId(), divider, mod)) {
                try {
                    invoicingService.handleEvents(processingEvent, payload);
                } catch (RuntimeException e) {
                    log.error("Error when polling invoicing event with id={}", processingEvent.getId(), e);
                    return EventAction.DELAYED_RETRY;
                }
            }
        }
        return EventAction.CONTINUE;
    }
}