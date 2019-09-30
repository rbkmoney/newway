package com.rbkmoney.newway.dao.dominant.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.domain.tables.pojos.Currency;
import com.rbkmoney.newway.domain.tables.records.CurrencyRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.Tables.CURRENCY;

@Component
public class CurrencyDaoImpl extends AbstractGenericDao implements DomainObjectDao<Currency, String> {

    public CurrencyDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long save(Currency currency) throws DaoException {
        CurrencyRecord currencyRecord = getDslContext().newRecord(CURRENCY, currency);
        Query query = getDslContext().insertInto(CURRENCY).set(currencyRecord).returning(CURRENCY.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public void updateNotCurrent(String currencyId) throws DaoException {
        Query query = getDslContext().update(CURRENCY).set(CURRENCY.CURRENT, false)
                .where(CURRENCY.CURRENCY_REF_ID.eq(currencyId).and(CURRENCY.CURRENT));
        executeOne(query);
    }
}
