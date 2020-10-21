package com.rbkmoney.newway.dao;

import com.rbkmoney.newway.dao.withdrawal.iface.WithdrawalDao;
import com.rbkmoney.newway.domain.tables.pojos.Withdrawal;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class WithdrawalDaoTest extends AbstractAppDaoTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WithdrawalDao withdrawalDao;

    @Test
    public void withdrawalDaoTest() {
        jdbcTemplate.execute("truncate table nw.withdrawal cascade");
        Withdrawal withdrawal = random(Withdrawal.class);
        withdrawal.setCurrent(true);
        Long id = withdrawalDao.save(withdrawal).get();
        withdrawal.setId(id);
        Withdrawal actual = withdrawalDao.get(withdrawal.getWithdrawalId());
        assertEquals(withdrawal, actual);
        withdrawalDao.updateNotCurrent(actual.getId());
        assertNull(withdrawalDao.get(withdrawal.getWithdrawalId()));

        //check duplicate not error
        withdrawalDao.save(withdrawal);
    }

}
