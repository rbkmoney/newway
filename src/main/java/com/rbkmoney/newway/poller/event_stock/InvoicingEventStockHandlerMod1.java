package com.rbkmoney.newway.poller.event_stock;

import com.rbkmoney.newway.service.InvoicingService;

import org.springframework.stereotype.Component;

@Component
public class InvoicingEventStockHandlerMod1 extends InvoicingEventStockHandler {

    private static final int MOD_1 = 1;

    public InvoicingEventStockHandlerMod1(InvoicingService invoicingService) {
        super(invoicingService);
    }

    @Override
    public int getMod() {
        return MOD_1;
    }
}
