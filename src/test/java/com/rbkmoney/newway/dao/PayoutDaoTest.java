package com.rbkmoney.newway.dao;

import com.rbkmoney.newway.dao.payout.iface.PayoutDao;
import com.rbkmoney.newway.domain.tables.pojos.Payout;
import com.rbkmoney.newway.exception.NotFoundException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.Assert.assertEquals;

public class PayoutDaoTest extends AbstractAppDaoTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PayoutDao payoutDao;


    @Test(expected = NotFoundException.class)
    public void payoutDaoTest() {
        jdbcTemplate.execute("truncate table nw.payout cascade");
        Payout payout = random(Payout.class);
        payout.setCurrent(true);
        Optional<Long> save = payoutDao.save(payout);
        Payout payoutGet = payoutDao.get(payout.getPayoutId());
        assertEquals(payout, payoutGet);
        payoutDao.updateNotCurrent(save.get());

        //check duplicate not error
        payoutDao.save(payout);

        payoutDao.get(payout.getPayoutId());
    }

    @Test
    public void payoutSummaryDaoTest() {
        jdbcTemplate.execute("truncate table nw.payout_summary cascade");
        Payout payout = random(Payout.class);
        payout.setCurrent(true);
        Long pytId = payoutDao.save(payout).get();
    }
}
