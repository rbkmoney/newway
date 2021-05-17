package com.rbkmoney.newway.handler.dominant.impl;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.newway.dao.dominant.impl.ProviderDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.*;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.*;

public class ProviderHandlerTest {

    @Mock
    private ProviderDaoImpl providerDao;

    @Before
    public void setUp() throws Exception {
        providerDao = Mockito.mock(ProviderDaoImpl.class);
    }

    @Test
    public void convertToDatabaseObjectTest() throws IOException {
        ProviderObject providerObject = buildProviderObject();
        ProviderHandler providerHandler = new ProviderHandler(providerDao);
        providerHandler.setDomainObject(DomainObject.provider(providerObject));
        com.rbkmoney.newway.domain.tables.pojos.Provider provider =
                providerHandler.convertToDatabaseObject(providerObject, 1L, true);
        assertNotNull(provider);
        assertEquals(provider.getName(), providerObject.getData().getName());
        assertEquals(provider.getIdentity(), providerObject.getData().getIdentity());
        assertEquals(provider.getDescription(), providerObject.getData().getDescription());
        assertEquals(provider.getAbsAccount(), providerObject.getData().getAbsAccount());
        assertFalse(provider.getPaymentTermsJson().isEmpty());
        assertFalse(provider.getRecurrentPaytoolTermsJson().isEmpty());
    }

    private ProviderObject buildProviderObject() throws IOException {
        return new ProviderObject()
                .setRef(new ProviderRef(1))
                .setData(new Provider()
                        .setName(random(String.class))
                        .setDescription(random(String.class))
                        .setProxy(new Proxy()
                                .setRef(new ProxyRef(random(Integer.class)))
                                .setAdditional(Map.of(random(String.class), random(String.class)))
                        )
                        .setIdentity(random(String.class))
                        .setAccounts(
                                Map.of(new CurrencyRef(random(String.class)), new ProviderAccount(random(Long.class))))
                        .setTerms(new ProvisionTermSet()
                                .setPayments(buildProvisionTermSet())
                                .setRecurrentPaytools(buildRecurrentPaytools())
                        )
                        .setAbsAccount(random(String.class))
                        .setParamsSchema(Collections.singletonList(buildProviderParameter()))
                );
    }

    private PaymentsProvisionTerms buildProvisionTermSet() throws IOException {
        CashFlowPosting cashFlowPosting = new CashFlowPosting();
        cashFlowPosting.setSource(buildMerchantCashFlowAccount());
        cashFlowPosting.setDestination(buildProviderCashFlowAccount());
        CashVolume cashVolume = new CashVolume();
        cashVolume.setFixed(new CashVolumeFixed().setCash(new Cash(1000L, new CurrencyRef("RUB"))));
        cashFlowPosting.setVolume(cashVolume);
        PaymentChargebackProvisionTerms paymentChargebackProvisionTerms = new PaymentChargebackProvisionTerms();
        PaymentsProvisionTerms provisionTermSet = new PaymentsProvisionTerms();
        paymentChargebackProvisionTerms.setCashFlow(CashFlowSelector.value(List.of(cashFlowPosting)));
        provisionTermSet.setChargebacks(paymentChargebackProvisionTerms);
        return provisionTermSet;
    }

    private RecurrentPaytoolsProvisionTerms buildRecurrentPaytools() throws IOException {
        RecurrentPaytoolsProvisionTerms recurrentPaytoolsProvisionTerms = new RecurrentPaytoolsProvisionTerms();
        PaymentMethodSelector paymentMethodSelector = new PaymentMethodSelector();
        paymentMethodSelector.setValue(Set.of(new PaymentMethodRef(
                PaymentMethod.bank_card(new BankCardPaymentMethod()
                        .setPaymentSystemDeprecated(LegacyBankCardPaymentSystem.visa)))));
        recurrentPaytoolsProvisionTerms.setPaymentMethods(paymentMethodSelector);
        CashValueSelector cashValueSelector = new CashValueSelector();
        cashValueSelector.setValue(buildCash(1000L));
        recurrentPaytoolsProvisionTerms.setCashValue(cashValueSelector);
        recurrentPaytoolsProvisionTerms.setCategories(CategorySelector.value(Set.of(new CategoryRef(123))));
        return recurrentPaytoolsProvisionTerms;
    }

    private ProviderParameter buildProviderParameter() throws IOException {
        ProviderParameter providerParameter = new ProviderParameter();
        providerParameter.setId(random(String.class));
        providerParameter.setDescription(random(String.class));
        providerParameter.setType(ProviderParameterType.string_type(new ProviderParameterString()));
        providerParameter.setIsRequired(true);
        return providerParameter;
    }

    private CashFlowAccount buildMerchantCashFlowAccount() {
        CashFlowAccount cashFlowAccount = new CashFlowAccount();
        cashFlowAccount.setMerchant(MerchantCashFlowAccount.guarantee);
        return cashFlowAccount;
    }

    private CashFlowAccount buildProviderCashFlowAccount() {
        CashFlowAccount cashFlowAccount = new CashFlowAccount();
        cashFlowAccount.setProvider(ProviderCashFlowAccount.settlement);
        return cashFlowAccount;
    }

    private Cash buildCash(long amount) {
        Cash cash = new Cash();
        cash.setAmount(amount);
        cash.setCurrency(new CurrencyRef("RUB"));
        return cash;
    }

}
