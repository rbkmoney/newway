package com.rbkmoney.newway.handler.dominant.impl;

import com.rbkmoney.damsel.domain.CountryObject;
import com.rbkmoney.damsel.domain.DomainObject;
import com.rbkmoney.damsel.domain.TradeBlocRef;
import com.rbkmoney.newway.TestData;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.dao.dominant.impl.CountryDaoImpl;
import com.rbkmoney.newway.domain.tables.pojos.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

class CountryHandlerTest {

    @Mock
    private CountryDaoImpl countryDao;

    private CountryHandler countryHandler;

    private CountryObject countryObject;

    @BeforeEach
    void setUp() {
        countryObject = TestData.buildCountryObject();
        countryHandler = new CountryHandler(countryDao);
        countryHandler.setDomainObject(DomainObject.country(countryObject));
    }

    @Test
    void shouldReturnDao() {
        DomainObjectDao<Country, String> domainObjectDao = countryHandler.getDomainObjectDao();
        assertEquals(countryDao, domainObjectDao);
    }

    @Test
    void shouldReturnTargetObject() {
        CountryObject targetObject = countryHandler.getTargetObject();
        assertEquals(countryObject, targetObject);
    }

    @Test
    void shouldReturnTargetObjectRef() {
        String targetObjectRefId = countryHandler.getTargetObjectRefId();
        assertEquals(countryObject.getRef().getId().name(), targetObjectRefId);
    }

    @Test
    void shouldAcceptDomainObject() {
        assertTrue(countryHandler.acceptDomainObject());
    }

    @Test
    void shouldConvertToDatabaseObject() {
        long versionId = 1L;
        boolean current = false;
        Country country = countryHandler.convertToDatabaseObject(countryObject, versionId, current);
        assertNotNull(country);
        assertEquals(countryObject.getData().getName(), country.getName());
        assertEquals(versionId, country.getVersionId());
        assertEquals(current, country.getCurrent());
        assertEquals(countryObject.getRef().getId().name(), country.getCountryRefId());
        assertArrayEquals(
                countryObject.getData().getTradeBlocs().stream().map(TradeBlocRef::getId).toArray(String[]::new),
                country.getTradeBloc());

    }
}