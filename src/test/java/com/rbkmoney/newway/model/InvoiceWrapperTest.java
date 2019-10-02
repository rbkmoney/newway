package com.rbkmoney.newway.model;

import org.junit.Test;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.*;

public class InvoiceWrapperTest {

    @Test
    public void copyTest() {
        InvoiceWrapper invoiceWrapper = random(InvoiceWrapper.class);
        InvoiceWrapper copy = invoiceWrapper.copy();
        invoiceWrapper.getInvoice().setInvoiceId("kek");
        invoiceWrapper.getCarts().get(0).setInvId(124L);
        assertNotEquals(invoiceWrapper.getInvoice().getInvoiceId(), copy.getInvoice().getInvoiceId());
        assertNotEquals(invoiceWrapper.getCarts().get(0).getInvId(), copy.getInvoice().getInvoiceId());

    }
}
