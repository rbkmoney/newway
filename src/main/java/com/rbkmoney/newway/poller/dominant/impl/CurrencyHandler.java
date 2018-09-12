package com.rbkmoney.newway.poller.dominant.impl;

import com.rbkmoney.damsel.domain.CurrencyObject;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.dao.dominant.impl.CurrencyDaoImpl;
import com.rbkmoney.newway.domain.tables.pojos.Currency;
import com.rbkmoney.newway.poller.dominant.AbstractDominantHandler;
import org.springframework.stereotype.Component;

@Component
public class CurrencyHandler extends AbstractDominantHandler<CurrencyObject, Currency, String> {

    private final CurrencyDaoImpl CurrencyDao;

    public CurrencyHandler(CurrencyDaoImpl CurrencyDao) {
        this.CurrencyDao = CurrencyDao;
    }

    @Override
    protected DomainObjectDao<Currency, String> getDomainObjectDao() {
        return CurrencyDao;
    }

    @Override
    protected CurrencyObject getObject() {
        return getDomainObject().getCurrency();
    }

    @Override
    protected String getObjectRefId() {
        return getDomainObject().getCurrency().getRef().getSymbolicCode();
    }

    @Override
    protected boolean acceptDomainObject() {
        return getDomainObject().isSetCurrency();
    }

    @Override
    public Currency convertToDatabaseObject(CurrencyObject currencyObject, Long versionId, boolean current) {
        Currency currency = new Currency();
        currency.setVersionId(versionId);
        currency.setCurrencyRefId(currencyObject.getRef().getSymbolicCode());
        com.rbkmoney.damsel.domain.Currency data = currencyObject.getData();
        currency.setName(data.getName());
        currency.setSymbolicCode(data.getSymbolicCode());
        currency.setNumericCode(data.getNumericCode());
        currency.setExponent(data.getExponent());
        currency.setCurrent(current);
        return currency;
    }
}
