package com.rbkmoney.newway.dao.deposit.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.common.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.deposit.iface.DepositDao;
import com.rbkmoney.newway.domain.tables.pojos.Deposit;
import com.rbkmoney.newway.domain.tables.records.DepositRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.tables.Deposit.DEPOSIT;

@Component
public class DepositDaoImpl extends AbstractGenericDao implements DepositDao {

    private final RowMapper<Deposit> depositRowMapper;

    @Autowired
    public DepositDaoImpl(DataSource dataSource) {
        super(dataSource);
        depositRowMapper = new RecordRowMapper<>(DEPOSIT, Deposit.class);
    }

    @Override
    public Long getLastEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(DEPOSIT.EVENT_ID)).from(DEPOSIT);
        return fetchOne(query, Long.class);
    }

    @Override
    public Long save(Deposit deposit) throws DaoException {
        DepositRecord record = getDslContext().newRecord(DEPOSIT, deposit);
        Query query = getDslContext().insertInto(DEPOSIT).set(record).returning(DEPOSIT.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOneWithReturn(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Deposit get(String depositId) throws DaoException {
        Query query = getDslContext().selectFrom(DEPOSIT)
                .where(DEPOSIT.DEPOSIT_ID.eq(depositId)
                        .and(DEPOSIT.CURRENT));

        return fetchOne(query, depositRowMapper);
    }

    @Override
    public void updateNotCurrent(String depositId) throws DaoException {
        Query query = getDslContext().update(DEPOSIT).set(DEPOSIT.CURRENT, false)
                .where(
                        DEPOSIT.DEPOSIT_ID.eq(depositId)
                                .and(DEPOSIT.CURRENT)
                );
        execute(query);
    }
}
