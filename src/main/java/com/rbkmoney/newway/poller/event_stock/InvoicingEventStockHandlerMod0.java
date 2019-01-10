package com.rbkmoney.newway.poller.event_stock;

import com.rbkmoney.newway.service.InvoicingService;
import org.springframework.stereotype.Component;

@Component
public class InvoicingEventStockHandlerMod0 extends InvoicingEventStockHandler {

    private static final int MOD_0 = 0;

    public InvoicingEventStockHandlerMod0(InvoicingService invoicingService) {
        super(invoicingService);
    }

    @Override
    public int getMod() {
        return MOD_0;
    }
}
