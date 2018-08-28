package com.rbkmoney.newway.poller.handler.impl.invoicing;

import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.newway.poller.handler.Handler;

public abstract class AbstractInvoicingHandler implements Handler<InvoiceChange, Event> {
}
