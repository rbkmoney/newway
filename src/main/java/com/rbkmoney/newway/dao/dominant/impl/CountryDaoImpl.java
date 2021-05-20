package com.rbkmoney.newway.dao.dominant.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.domain.tables.pojos.Country;
import com.rbkmoney.newway.domain.tables.records.CountryRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.Tables.COUNTRY;

@Component
public class CountryDaoImpl extends AbstractGenericDao implements DomainObjectDao<Country, String> {

    public CountryDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long save(Country country) throws DaoException {
        CountryRecord countryRecord = getDslContext().newRecord(COUNTRY, country);
        Query query = getDslContext().insertInto(COUNTRY).set(countryRecord).returning(COUNTRY.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public void updateNotCurrent(String countryId) throws DaoException {
        Query query = getDslContext().update(COUNTRY).set(COUNTRY.CURRENT, false)
                .where(COUNTRY.COUNTRY_REF_ID.eq(countryId).and(COUNTRY.CURRENT));
        executeOne(query);
    }
}
