package com.rbkmoney.newway.handler.event.stock.impl.invoicing;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.handler.event.stock.Handler;

public interface InvoicingHandler extends Handler<InvoiceChange, MachineEvent> {
}
