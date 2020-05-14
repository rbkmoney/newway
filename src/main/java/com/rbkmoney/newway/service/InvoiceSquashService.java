package com.rbkmoney.newway.service;

import com.rbkmoney.newway.model.InvoiceWrapper;
import org.springframework.stereotype.Service;

@Service
public class InvoiceSquashService extends SquashService<InvoiceWrapper> {
    @Override
    protected void setId(InvoiceWrapper invoiceWrapper, Long id) {
        invoiceWrapper.getInvoice().setId(id);
        if (invoiceWrapper.getCarts() != null) {
            invoiceWrapper.getCarts().forEach(c -> {
                c.setId(null);
                c.setInvId(id);
            });
        }
    }

    @Override
    protected Long getId(InvoiceWrapper invoiceWrapper) {
        return invoiceWrapper.getInvoice().getId();
    }
}
