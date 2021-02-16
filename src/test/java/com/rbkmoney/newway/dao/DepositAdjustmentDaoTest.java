package com.rbkmoney.newway.dao;

import com.rbkmoney.newway.dao.deposit_adjustment.iface.DepositAdjustmentDao;
import com.rbkmoney.newway.domain.tables.pojos.DepositAdjustment;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class DepositAdjustmentDaoTest extends AbstractAppDaoTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DepositAdjustmentDao depositadjustmentDao;

    @Test
    public void depositAdjustmentTest() {
        jdbcTemplate.execute("truncate table nw.deposit_adjustment cascade");
        DepositAdjustment deposit = random(DepositAdjustment.class);
        deposit.setCurrent(true);
        Long id = depositadjustmentDao.save(deposit).get();
        deposit.setId(id);
        DepositAdjustment actual = depositadjustmentDao.get(deposit.getDepositId(), deposit.getAdjustmentId());
        assertEquals(deposit, actual);
        depositadjustmentDao.updateNotCurrent(actual.getId());
        assertNull(depositadjustmentDao.get(deposit.getDepositId(), deposit.getAdjustmentId()));

        //check duplicate not error
        depositadjustmentDao.save(deposit);
    }

}
