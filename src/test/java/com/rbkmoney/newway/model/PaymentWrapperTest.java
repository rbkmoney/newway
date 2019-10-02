package com.rbkmoney.newway.model;

import org.junit.Test;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertNotEquals;

public class PaymentWrapperTest {

    @Test
    public void copyTest() {
        PaymentWrapper paymentWrapper = random(PaymentWrapper.class);
        PaymentWrapper copy = paymentWrapper.copy();
        paymentWrapper.getPayment().setInvoiceId("kek");
        paymentWrapper.getCashFlows().get(0).setObjId(124L);
        assertNotEquals(paymentWrapper.getPayment().getInvoiceId(), copy.getPayment().getInvoiceId());
        assertNotEquals(paymentWrapper.getCashFlows().get(0).getObjId(), copy.getCashFlows().get(0).getObjId());
    }
}
