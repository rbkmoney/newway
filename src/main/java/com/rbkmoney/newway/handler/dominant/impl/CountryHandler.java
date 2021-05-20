package com.rbkmoney.newway.handler.dominant.impl;

import com.rbkmoney.damsel.domain.CountryObject;
import com.rbkmoney.damsel.domain.TradeBlocRef;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.dao.dominant.impl.CountryDaoImpl;
import com.rbkmoney.newway.domain.tables.pojos.Country;
import com.rbkmoney.newway.handler.dominant.AbstractDominantHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CountryHandler extends AbstractDominantHandler<CountryObject, Country, String> {

    private final CountryDaoImpl countryDao;

    @Override
    protected DomainObjectDao<Country, String> getDomainObjectDao() {
        return countryDao;
    }

    @Override
    protected CountryObject getTargetObject() {
        return getDomainObject().getCountry();
    }

    @Override
    protected String getTargetObjectRefId() {
        return getTargetObject().getRef().getId().name();
    }

    @Override
    protected boolean acceptDomainObject() {
        return getDomainObject().isSetCountry();
    }

    @Override
    public Country convertToDatabaseObject(CountryObject object, Long versionId, boolean current) {
        Country country = new Country();
        country.setVersionId(versionId);
        country.setCountryRefId(getTargetObjectRefId());
        com.rbkmoney.damsel.domain.Country data = object.getData();
        country.setName(data.getName());
        String[] tradeBlocs = data.getTradeBlocs().stream().map(TradeBlocRef::getId).toArray(String[]::new);
        country.setTradeBloc(tradeBlocs);
        country.setCurrent(current);
        return country;
    }
}
