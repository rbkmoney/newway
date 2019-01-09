package com.rbkmoney.newway.poller.event_stock;

import com.rbkmoney.newway.service.InvoicingService;

import org.springframework.stereotype.Service;

@Service
public class InvoicingMod1EventStockHandler extends InvoicingEventStockHandler {

    private static final int MODULO = 1;

    public InvoicingMod1EventStockHandler(InvoicingService invoicingService) {
        super(invoicingService);
    }

    @Override
    public int getMod() {
        return MODULO;
    }
}
