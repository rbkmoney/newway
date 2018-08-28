package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.invoicing.iface.RefundDao;
import com.rbkmoney.newway.domain.tables.pojos.Refund;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.*;

public class RefundDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private RefundDao refundDao;

    @Test
    public void test() {
        Refund refund = random(Refund.class);
        refund.setCurrent(true);
        refundDao.save(refund);
        Refund refundGet = refundDao.get(refund.getInvoiceId(), refund.getPaymentId(), refund.getRefundId());
        assertEquals(refund, refundGet);
        refundDao.updateNotCurrent(refund.getInvoiceId(), refund.getPaymentId(), refund.getRefundId());
        assertNull(refundDao.get(refund.getInvoiceId(), refund.getPaymentId(), refund.getRefundId()));
    }
}