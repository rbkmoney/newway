package com.rbkmoney.newway.dao.rate.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.rate.iface.RateDao;
import com.rbkmoney.newway.domain.tables.pojos.Rate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class RateDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private RateDao rateDao;

    @Test
    public void test() {
        Rate rate = random(Rate.class);
        rate.setCurrent(true);
        Long id = rateDao.save(rate);
        rate.setId(id);
        assertEquals(rate, rateDao.get(rate.getEventSourceId()));

        rateDao.updateNotCurrent(rate.getEventSourceId());
        assertNull(rateDao.get(rate.getEventSourceId()));
    }
}
