package com.rbkmoney.newway.dao.dominant.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.domain.tables.pojos.WithdrawalProvider;
import com.rbkmoney.newway.domain.tables.records.WithdrawalProviderRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.Tables.WITHDRAWAL_PROVIDER;

@Component
public class WithdrawalProviderDaoImpl extends AbstractGenericDao implements DomainObjectDao<WithdrawalProvider, Integer> {

    public WithdrawalProviderDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long save(WithdrawalProvider provider) throws DaoException {
        WithdrawalProviderRecord withdrawalProviderRecord = getDslContext().newRecord(WITHDRAWAL_PROVIDER, provider);
        Query query = getDslContext().insertInto(WITHDRAWAL_PROVIDER).set(withdrawalProviderRecord).returning(WITHDRAWAL_PROVIDER.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public void updateNotCurrent(Integer providerId) throws DaoException {
        Query query = getDslContext().update(WITHDRAWAL_PROVIDER).set(WITHDRAWAL_PROVIDER.CURRENT, false)
                .where(WITHDRAWAL_PROVIDER.WITHDRAWAL_PROVIDER_REF_ID.eq(providerId).and(WITHDRAWAL_PROVIDER.CURRENT));
        executeOne(query);
    }
}
