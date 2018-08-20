package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.invoicing.iface.AdjustmentDao;
import com.rbkmoney.newway.domain.tables.pojos.Adjustment;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.*;

public class AdjustmentDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private AdjustmentDao adjustmentDao;

    @Test
    public void test() {
        Adjustment adjustment = random(Adjustment.class);
        adjustment.setCurrent(true);
        adjustmentDao.save(adjustment);
        assertEquals(adjustment.getPartyId(), adjustmentDao.get(adjustment.getInvoiceId(), adjustment.getPaymentId(), adjustment.getAdjustmentId()).getPartyId());
        adjustmentDao.update(adjustment.getInvoiceId(), adjustment.getPaymentId(), adjustment.getAdjustmentId());
        assertNull(adjustmentDao.get(adjustment.getInvoiceId(), adjustment.getPaymentId(), adjustment.getAdjustmentId()));
    }
}