package com.rbkmoney.newway.poller.dominant.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.dominant.impl.CurrencyDaoImpl;
import com.rbkmoney.newway.domain.tables.pojos.Currency;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

public class CurrencyHandlerTest extends AbstractIntegrationTest {

    @Autowired
    private CurrencyDaoImpl currencyDao;

    @Test
    public void test() {
        Currency category = random(Currency.class);
        category.setCurrent(true);
        currencyDao.save(category);
        currencyDao.updateNotCurrent(category.getCurrencyRefId());
    }
}
