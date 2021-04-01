package com.rbkmoney.newway.dao;

import com.rbkmoney.newway.dao.withdrawal.iface.WithdrawalDao;
import com.rbkmoney.newway.domain.tables.pojos.Withdrawal;
import com.rbkmoney.newway.exception.NotFoundException;
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

    @Test(expected = NotFoundException.class)
    public void withdrawalDaoTest() {
        jdbcTemplate.execute("truncate table nw.withdrawal cascade");
        Withdrawal withdrawal = random(Withdrawal.class);
        withdrawal.setCurrent(true);
        Long id = withdrawalDao.save(withdrawal).get();
        withdrawal.setId(id);
        Withdrawal actual = withdrawalDao.get(withdrawal.getWithdrawalId());
        assertEquals(withdrawal, actual);
        withdrawalDao.updateNotCurrent(actual.getId());

        //check duplicate not error
        withdrawalDao.save(withdrawal);

        withdrawalDao.get(withdrawal.getWithdrawalId());
    }

}
