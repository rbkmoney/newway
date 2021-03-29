package com.rbkmoney.newway.handler.dominant.impl;

import com.rbkmoney.damsel.domain.CurrencyObject;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.dao.dominant.impl.CurrencyDaoImpl;
import com.rbkmoney.newway.domain.tables.pojos.Currency;
import com.rbkmoney.newway.handler.dominant.AbstractDominantHandler;
import org.springframework.stereotype.Component;

@Component
public class CurrencyHandler extends AbstractDominantHandler<CurrencyObject, Currency, String> {

    private final CurrencyDaoImpl currencyDao;

    public CurrencyHandler(CurrencyDaoImpl currencyDao) {
        this.currencyDao = currencyDao;
    }

    @Override
    protected DomainObjectDao<Currency, String> getDomainObjectDao() {
        return currencyDao;
    }

    @Override
    protected CurrencyObject getTargetObject() {
        return getDomainObject().getCurrency();
    }

    @Override
    protected String getTargetObjectRefId() {
        return getTargetObject().getRef().getSymbolicCode();
    }

    @Override
    protected boolean acceptDomainObject() {
        return getDomainObject().isSetCurrency();
    }

    @Override
    public Currency convertToDatabaseObject(CurrencyObject currencyObject, Long versionId, boolean current) {
        Currency currency = new Currency();
        currency.setVersionId(versionId);
        currency.setCurrencyRefId(getTargetObjectRefId());
        com.rbkmoney.damsel.domain.Currency data = currencyObject.getData();
        currency.setName(data.getName());
        currency.setSymbolicCode(data.getSymbolicCode());
        currency.setNumericCode(data.getNumericCode());
        currency.setExponent(data.getExponent());
        currency.setCurrent(current);
        return currency;
    }
}
