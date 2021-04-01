package com.rbkmoney.newway.mapper;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;

public abstract class AbstractInvoicingMapper<M> implements Mapper<InvoiceChange, MachineEvent, M> {
}
