package com.rbkmoney.newway.dao.deposit.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.deposit.iface.DepositDao;
import com.rbkmoney.newway.domain.tables.pojos.Deposit;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class DepositImplTest extends AbstractIntegrationTest {

    @Autowired
    private DepositDao depositDao;

    @Test
    public void saveAndGetTest() {
        Deposit deposit = random(Deposit.class);
        deposit.setCurrent(true);
        Long id = depositDao.save(deposit);
        deposit.setId(id);
        assertEquals(deposit, depositDao.get(deposit.getDepositId()));
        depositDao.updateNotCurrent(deposit.getDepositId());
        assertNull(depositDao.get(deposit.getDepositId()));
    }

}
