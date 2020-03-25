package com.rbkmoney.newway.poller.dominant.impl;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.newway.dao.dominant.impl.WithdrawalProviderDaoImpl;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.*;

public class TemporaryWithdrawalProviderHandlerTest {

    @Mock
    private WithdrawalProviderDaoImpl withdrawalProviderDao;

    @Test
    public void convertToDatabaseObject() {
        WithdrawalProviderObject withdrawalProviderObject = buildWithdrawalProviderObject();

        TemporaryWithdrawalProviderHandler handler = new TemporaryWithdrawalProviderHandler(withdrawalProviderDao);
        handler.setDomainObject(DomainObject.withdrawal_provider(withdrawalProviderObject));
        var withdrawalProvider = handler.convertToDatabaseObject(withdrawalProviderObject, 123L, true);
        assertNotNull(withdrawalProvider);
        assertEquals(withdrawalProvider.getName(), withdrawalProviderObject.getData().getName());
        assertEquals(withdrawalProvider.getIdentity(), withdrawalProviderObject.getData().getIdentity());
        assertTrue(withdrawalProvider.getAccountsJson().contains("RUB"));
    }

    private WithdrawalProviderObject buildWithdrawalProviderObject() {
        return new WithdrawalProviderObject()
                .setRef(new WithdrawalProviderRef(1))
                .setData(new WithdrawalProvider()
                        .setName(random(String.class))
                .setDescription(random(String.class))
                .setProxy(new Proxy()
                .setRef(new ProxyRef(random(Integer.class)))
                .setAdditional(Map.of(random(String.class), random(String.class))))
                .setIdentity(random(String.class))
                .setAccounts(Map.of(new CurrencyRef("RUB"), new ProviderAccount(123))));
    }
}
