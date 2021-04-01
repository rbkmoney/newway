package com.rbkmoney.newway.dao;

import com.rbkmoney.newway.dao.deposit.iface.DepositDao;
import com.rbkmoney.newway.domain.tables.pojos.Deposit;
import com.rbkmoney.newway.exception.NotFoundException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class DepositDaoTest extends AbstractAppDaoTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DepositDao depositDao;

    @Test(expected = NotFoundException.class)
    public void depositDaoTest() {
        jdbcTemplate.execute("truncate table nw.deposit cascade");
        Deposit deposit = random(Deposit.class);
        deposit.setCurrent(true);
        Long id = depositDao.save(deposit).get();
        deposit.setId(id);
        Deposit actual = depositDao.get(deposit.getDepositId());
        assertEquals(deposit, actual);
        depositDao.updateNotCurrent(actual.getId());

        //check duplicate not error
        depositDao.save(deposit);

        depositDao.get(deposit.getDepositId());
    }

}
