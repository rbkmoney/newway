package com.rbkmoney.newway.dao;

import com.rbkmoney.newway.dao.withdrawal.session.iface.WithdrawalSessionDao;
import com.rbkmoney.newway.domain.tables.pojos.WithdrawalSession;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class WithdrawalSessionDaoTest extends AbstractAppDaoTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WithdrawalSessionDao withdrawalSessionDao;

    @Test
    public void withdrawalSessionDao() {
        jdbcTemplate.execute("truncate table nw.withdrawal_session cascade");
        WithdrawalSession withdrawalSession = random(WithdrawalSession.class);
        withdrawalSession.setCurrent(true);
        Long id = withdrawalSessionDao.save(withdrawalSession).get();
        withdrawalSession.setId(id);
        WithdrawalSession actual = withdrawalSessionDao.get(withdrawalSession.getWithdrawalSessionId());
        assertEquals(withdrawalSession, actual);
        withdrawalSessionDao.updateNotCurrent(actual.getId());
        assertNull(withdrawalSessionDao.get(withdrawalSession.getWithdrawalSessionId()));

        //check duplicate not error
        withdrawalSessionDao.save(withdrawalSession);
    }

}
