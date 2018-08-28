package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.*;

public class PaymentDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private PaymentDao paymentDao;

    @Test
    public void test() {
        Payment payment = random(Payment.class);
        payment.setCurrent(true);
        paymentDao.save(payment);
        Payment paymentGet = paymentDao.get(payment.getInvoiceId(), payment.getPaymentId());
        assertEquals(payment, paymentGet);
        paymentDao.updateNotCurrent(payment.getInvoiceId(), payment.getPaymentId());
        assertNull(paymentDao.get(payment.getInvoiceId(), payment.getPaymentId()));
    }
}