package com.rbkmoney.newway.dao.dominant.iface;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.dominant.impl.*;
import com.rbkmoney.newway.domain.tables.pojos.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.OptionalLong;
import java.util.stream.LongStream;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.*;

public class DomainObjectDaoTest extends AbstractIntegrationTest {

    @Autowired
    private CalendarDaoImpl calendarDao;
    @Autowired
    private CategoryDaoImpl categoryDao;
    @Autowired
    private CurrencyDaoImpl currencyDao;
    @Autowired
    private InspectorDaoImpl inspectorDao;
    @Autowired
    private PaymentInstitutionDaoImpl paymentInstitutionDao;
    @Autowired
    private PaymentMethodDaoImpl paymentMethodDao;
    @Autowired
    private PayoutMethodDaoImpl payoutMethodDao;
    @Autowired
    private ProviderDaoImpl providerDao;
    @Autowired
    private ProxyDaoImpl proxyDao;
    @Autowired
    private TerminalDaoImpl terminalDao;
    @Autowired
    private TermSetHierarchyDaoImpl termSetHierarchyDao;
    @Autowired
    private DominantDao dominantDao;

    @Test
    public void test() {
        Calendar calendar = random(Calendar.class);
        calendar.setCurrent(true);
        calendarDao.save(calendar);
        calendarDao.updateNotCurrent(calendar.getCalendarRefId());

        Category category = random(Category.class);
        category.setCurrent(true);
        categoryDao.save(category);
        categoryDao.updateNotCurrent(category.getCategoryRefId());

        Currency currency = random(Currency.class);
        currency.setCurrent(true);
        currencyDao.save(currency);
        currencyDao.updateNotCurrent(currency.getCurrencyRefId());

        Inspector inspector = random(Inspector.class);
        inspector.setCurrent(true);
        inspectorDao.save(inspector);
        inspectorDao.updateNotCurrent(inspector.getInspectorRefId());

        PaymentInstitution paymentInstitution = random(PaymentInstitution.class);
        paymentInstitution.setCurrent(true);
        paymentInstitutionDao.save(paymentInstitution);
        paymentInstitutionDao.updateNotCurrent(paymentInstitution.getPaymentInstitutionRefId());

        PaymentMethod paymentMethod = random(PaymentMethod.class);
        paymentMethod.setCurrent(true);
        paymentMethodDao.save(paymentMethod);
        paymentMethodDao.updateNotCurrent(paymentMethod.getPaymentMethodRefId());

        PayoutMethod payoutMethod = random(PayoutMethod.class);
        payoutMethod.setCurrent(true);
        payoutMethodDao.save(payoutMethod);
        payoutMethodDao.updateNotCurrent(payoutMethod.getPayoutMethodRefId());

        Provider provider = random(Provider.class);
        provider.setCurrent(true);
        providerDao.save(provider);
        providerDao.updateNotCurrent(provider.getProviderRefId());

        Proxy proxy = random(Proxy.class);
        proxy.setCurrent(true);
        proxyDao.save(proxy);
        proxyDao.updateNotCurrent(proxy.getProxyRefId());

        Terminal terminal = random(Terminal.class);
        terminal.setCurrent(true);
        terminalDao.save(terminal);
        terminalDao.updateNotCurrent(terminal.getTerminalRefId());

        TermSetHierarchy termSetHierarchy = random(TermSetHierarchy.class);
        termSetHierarchy.setCurrent(true);
        termSetHierarchyDao.save(termSetHierarchy);
        termSetHierarchyDao.updateNotCurrent(termSetHierarchy.getTermSetHierarchyRefId());

        Long lastVersionId = dominantDao.getLastVersionId();

        OptionalLong maxVersionId = LongStream.of(
                calendar.getVersionId(),
                category.getVersionId(),
                currency.getVersionId(),
                inspector.getVersionId(),
                paymentInstitution.getVersionId(),
                paymentMethod.getVersionId(),
                payoutMethod.getVersionId(),
                provider.getVersionId(),
                proxy.getVersionId(),
                terminal.getVersionId(),
                termSetHierarchy.getVersionId()).max();

        assertEquals(maxVersionId.getAsLong(), lastVersionId.longValue());
    }
}
