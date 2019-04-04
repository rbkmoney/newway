package com.rbkmoney.newway.dao.withdrawal.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.common.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.withdrawal.iface.WithdrawalDao;
import com.rbkmoney.newway.domain.tables.pojos.Withdrawal;
import com.rbkmoney.newway.domain.tables.records.WithdrawalRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.tables.Withdrawal.WITHDRAWAL;

@Component
public class WithdrawalDaoImpl extends AbstractGenericDao implements WithdrawalDao {

    private final RowMapper<Withdrawal> withdrawalRowMapper;

    @Autowired
    public WithdrawalDaoImpl(DataSource dataSource) {
        super(dataSource);
        withdrawalRowMapper = new RecordRowMapper<>(WITHDRAWAL, Withdrawal.class);
    }

    @Override
    public Long getLastEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(WITHDRAWAL.EVENT_ID)).from(WITHDRAWAL);
        return fetchOne(query, Long.class);
    }

    @Override
    public Long save(Withdrawal withdrawal) throws DaoException {
        WithdrawalRecord record = getDslContext().newRecord(WITHDRAWAL, withdrawal);
        Query query = getDslContext().insertInto(WITHDRAWAL).set(record).returning(WITHDRAWAL.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOneWithReturn(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Withdrawal get(String withdrawalId) throws DaoException {
        Query query = getDslContext().selectFrom(WITHDRAWAL)
                .where(WITHDRAWAL.WITHDRAWAL_ID.eq(withdrawalId)
                        .and(WITHDRAWAL.CURRENT));

        return fetchOne(query, withdrawalRowMapper);
    }

    @Override
    public void updateNotCurrent(String withdrawalId) throws DaoException {
        Query query = getDslContext().update(WITHDRAWAL).set(WITHDRAWAL.CURRENT, false)
                .where(
                        WITHDRAWAL.WITHDRAWAL_ID.eq(withdrawalId)
                                .and(WITHDRAWAL.CURRENT)
                );
        execute(query);
    }
}
