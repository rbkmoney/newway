package com.rbkmoney.newway.dao.dominant.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.domain.tables.pojos.Provider;
import com.rbkmoney.newway.domain.tables.records.ProviderRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.Tables.PROVIDER;

@Component
public class ProviderDaoImpl extends AbstractGenericDao implements DomainObjectDao<Provider, Integer> {

    public ProviderDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long save(Provider provider) throws DaoException {
        ProviderRecord providerRecord = getDslContext().newRecord(PROVIDER, provider);
        Query query = getDslContext().insertInto(PROVIDER).set(providerRecord).returning(PROVIDER.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public void updateNotCurrent(Integer providerId) throws DaoException {
        Query query = getDslContext().update(PROVIDER).set(PROVIDER.CURRENT, false)
                .where(PROVIDER.PROVIDER_REF_ID.eq(providerId).and(PROVIDER.CURRENT));
        executeOne(query);
    }
}
