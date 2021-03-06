package com.rbkmoney.newway.dao;

import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.dao.payout.iface.PayoutDao;
import com.rbkmoney.newway.dao.payout.iface.PayoutSummaryDao;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import com.rbkmoney.newway.domain.tables.pojos.Payout;
import com.rbkmoney.newway.domain.tables.pojos.PayoutSummary;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class PayoutDaoTest extends AbstractAppDaoTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PayoutDao payoutDao;

    @Autowired
    private PayoutSummaryDao payoutSummaryDao;

    @Test
    public void payoutDaoTest() {
        jdbcTemplate.execute("truncate table nw.payout cascade");
        Payout payout = random(Payout.class);
        payout.setCurrent(true);
        Optional<Long> save = payoutDao.save(payout);
        Payout payoutGet = payoutDao.get(payout.getPayoutId());
        assertEquals(payout, payoutGet);
        payoutDao.updateNotCurrent(save.get());
        Assert.assertNull(payoutDao.get(payout.getPayoutId()));

        //check duplicate not error
        payoutDao.save(payout);
    }

    @Test
    public void payoutSummaryDaoTest() {
        jdbcTemplate.execute("truncate table nw.payout_summary cascade");
        Payout payout = random(Payout.class);
        payout.setCurrent(true);
        Long pytId = payoutDao.save(payout).get();
        List<PayoutSummary> payoutSummaries = randomListOf(10, PayoutSummary.class);
        payoutSummaries.forEach(pt -> pt.setPytId(pytId));
        payoutSummaryDao.save(payoutSummaries);
        List<PayoutSummary> byPytId = payoutSummaryDao.getByPytId(pytId);
        assertEquals(new HashSet(payoutSummaries), new HashSet(byPytId));
    }

}
