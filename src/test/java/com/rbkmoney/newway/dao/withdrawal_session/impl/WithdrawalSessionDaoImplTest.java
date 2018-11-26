package com.rbkmoney.newway.dao.withdrawal_session.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.withdrawal_session.iface.WithdrawalSessionDao;
import com.rbkmoney.newway.domain.tables.pojos.WithdrawalSession;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class WithdrawalSessionDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private WithdrawalSessionDao withdrawalSessionDao;

    @Test
    public void test() {
        WithdrawalSession withdrawalSession = random(WithdrawalSession.class);
        withdrawalSession.setCurrent(true);
        Long id = withdrawalSessionDao.save(withdrawalSession);
        withdrawalSession.setId(id);
        assertEquals(withdrawalSession, withdrawalSessionDao.get(withdrawalSession.getWithdrawalSessionId()));
        withdrawalSessionDao.updateNotCurrent(withdrawalSession.getWithdrawalSessionId());
        assertNull(withdrawalSessionDao.get(withdrawalSession.getWithdrawalSessionId()));
    }

}
