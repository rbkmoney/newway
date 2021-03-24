package com.rbkmoney.newway.dao;

import com.rbkmoney.newway.dao.deposit_revert.iface.DepositRevertDao;
import com.rbkmoney.newway.domain.tables.pojos.DepositRevert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

@Transactional
public class DepositRevertDaoTest extends AbstractAppDaoTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DepositRevertDao depositRevertDao;

    @Test
    public void depositRevertTest() {
        DepositRevert deposit = random(DepositRevert.class);
        deposit.setCurrent(true);
        Long id = depositRevertDao.save(deposit).get();
        deposit.setId(id);
        DepositRevert actual = depositRevertDao.get(deposit.getDepositId(), deposit.getRevertId());
        assertEquals(deposit, actual);
        depositRevertDao.updateNotCurrent(actual.getId());
        assertNull(depositRevertDao.get(deposit.getDepositId(), deposit.getRevertId()));

        //check duplicate not error
        depositRevertDao.save(deposit);
    }

}
