package com.rbkmoney.newway.poller.event.stock.impl.invoicing;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.poller.event.stock.Handler;

public abstract class AbstractInvoicingHandler implements Handler<InvoiceChange, MachineEvent> {
}
