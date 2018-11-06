package com.rbkmoney.newway.dao.identity.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class IdentityDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private IdentityDao identityDao;

    @Test
    public void test() {
        Identity identity = random(Identity.class);
        identity.setCurrent(true);
        Long id = identityDao.save(identity);
        identity.setId(id);
        assertEquals(identity, identityDao.get(identity.getIdentityId()));
        identityDao.updateNotCurrent(identity.getIdentityId());
        assertNull(identityDao.get(identity.getIdentityId()));
    }

}
