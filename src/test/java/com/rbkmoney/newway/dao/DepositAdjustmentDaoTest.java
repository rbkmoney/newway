package com.rbkmoney.newway.dao;

import com.rbkmoney.newway.dao.deposit_adjustment.iface.DepositAdjustmentDao;
import com.rbkmoney.newway.domain.tables.pojos.DepositAdjustment;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

@Transactional
public class DepositAdjustmentDaoTest extends AbstractAppDaoTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DepositAdjustmentDao depositAdjustmentDao;

    @Test
    public void depositAdjustmentTest() {
        DepositAdjustment deposit = random(DepositAdjustment.class);
        deposit.setAmount(null);
        deposit.setCurrencyCode(null);
        deposit.setCurrent(true);
        Long id = depositAdjustmentDao.save(deposit).get();
        deposit.setId(id);
        DepositAdjustment actual = depositAdjustmentDao.get(deposit.getDepositId(), deposit.getAdjustmentId());
        assertEquals(deposit, actual);
        depositAdjustmentDao.updateNotCurrent(actual.getId());
        assertNull(depositAdjustmentDao.get(deposit.getDepositId(), deposit.getAdjustmentId()));

        //check duplicate not error
        depositAdjustmentDao.save(deposit);
    }

}
