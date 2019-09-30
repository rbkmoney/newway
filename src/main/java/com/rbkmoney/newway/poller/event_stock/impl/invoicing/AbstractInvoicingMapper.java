package com.rbkmoney.newway.poller.event_stock.impl.invoicing;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.poller.event_stock.Mapper;

public abstract class AbstractInvoicingMapper<M> implements Mapper<InvoiceChange, MachineEvent, M> {
}
