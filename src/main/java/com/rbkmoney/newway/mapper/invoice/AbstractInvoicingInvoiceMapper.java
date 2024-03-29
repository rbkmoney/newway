package com.rbkmoney.newway.mapper.invoice;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.mapper.AbstractInvoicingMapper;
import com.rbkmoney.newway.model.InvoiceWrapper;

public abstract class AbstractInvoicingInvoiceMapper extends AbstractInvoicingMapper<InvoiceWrapper> {
    protected void setDefaultProperties(Invoice invoice, Long sequenceId, Integer changeId, String eventCreatedAt) {
        invoice.setId(null);
        invoice.setWtime(null);
        invoice.setCurrent(false);
        invoice.setChangeId(changeId);
        invoice.setSequenceId(sequenceId);
        invoice.setEventCreatedAt(TypeUtil.stringToLocalDateTime(eventCreatedAt));
    }
}
