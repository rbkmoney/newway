package com.rbkmoney.newway.poller.event_stock.impl.invoicing;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.poller.event_stock.Handler;

public abstract class AbstractInvoicingHandler implements Handler<InvoiceChange, MachineEvent> {
}
