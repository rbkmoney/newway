package com.rbkmoney.newway.dao.payout.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.payout.iface.PayoutDao;
import com.rbkmoney.newway.dao.payout.iface.PayoutSummaryDao;
import com.rbkmoney.newway.domain.tables.pojos.Payout;
import com.rbkmoney.newway.domain.tables.pojos.PayoutSummary;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.Assert.*;

public class PayoutSummaryDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private PayoutSummaryDao payoutSummaryDao;

    @Autowired
    private PayoutDao payoutDao;

    @Test
    public void test() {
        Payout payout = random(Payout.class);
        payout.setCurrent(true);
        Long pytId = payoutDao.save(payout);
        List<PayoutSummary> payoutSummaries = randomListOf(10, PayoutSummary.class);
        payoutSummaries.forEach(pt -> pt.setPytId(pytId));
        payoutSummaryDao.save(payoutSummaries);
        List<PayoutSummary> byPytId = payoutSummaryDao.getByPytId(pytId);
        assertEquals(new HashSet(payoutSummaries), new HashSet(byPytId));
    }
}