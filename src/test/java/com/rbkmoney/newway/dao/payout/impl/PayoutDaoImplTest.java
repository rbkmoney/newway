package com.rbkmoney.newway.dao.payout.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.payout.iface.PayoutDao;
import com.rbkmoney.newway.domain.tables.pojos.Payout;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.*;

public class PayoutDaoImplTest  extends AbstractIntegrationTest {

    @Autowired
    private PayoutDao PayoutDao;

    @Test
    public void test() {
        Payout payout = random(Payout.class);
        payout.setCurrent(true);
        PayoutDao.save(payout);
        Payout payoutGet = PayoutDao.get(payout.getPayoutId());
        assertEquals(payout, payoutGet);
        PayoutDao.updateNotCurrent(payout.getPayoutId());
        assertNull(PayoutDao.get(payout.getPayoutId()));
        assertEquals(PayoutDao.getLastEventId(), payout.getEventId());
    }
}