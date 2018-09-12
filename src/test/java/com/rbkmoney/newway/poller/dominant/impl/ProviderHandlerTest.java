package com.rbkmoney.newway.poller.dominant.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.dominant.impl.ProviderDaoImpl;
import com.rbkmoney.newway.domain.tables.pojos.Provider;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

public class ProviderHandlerTest extends AbstractIntegrationTest {

    @Autowired
    private ProviderDaoImpl providerDao;

    @Test
    public void test() {
        Provider category = random(Provider.class);
        category.setCurrent(true);
        providerDao.save(category);
        providerDao.updateNotCurrent(category.getProviderRefId());
    }
}
