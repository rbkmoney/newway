package com.rbkmoney.newway.poller.event_stock;

import com.rbkmoney.newway.service.InvoicingService;
import org.springframework.stereotype.Service;

@Service
public class InvoicingMod0EventStockHandler extends InvoicingEventStockHandler {

    private static final int MODULO = 0;

    public InvoicingMod0EventStockHandler(InvoicingService invoicingService) {
        super(invoicingService);
    }

    @Override
    public int getMod() {
        return MODULO;
    }
}
