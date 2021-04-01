package com.rbkmoney.newway.dao;

import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import com.rbkmoney.newway.exception.NotFoundException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class IdentityDaoTest extends AbstractAppDaoTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private IdentityDao identityDao;

    @Test(expected = NotFoundException.class)
    public void identityDaoTest() {
        jdbcTemplate.execute("truncate table nw.identity cascade");
        Identity identity = random(Identity.class);
        identity.setCurrent(true);
        Long id = identityDao.save(identity).get();
        identity.setId(id);
        Identity actual = identityDao.get(identity.getIdentityId());
        assertEquals(identity, actual);
        identityDao.updateNotCurrent(actual.getId());

        //check duplicate not error
        identityDao.save(identity);

        identityDao.get(identity.getIdentityId());
    }

}
