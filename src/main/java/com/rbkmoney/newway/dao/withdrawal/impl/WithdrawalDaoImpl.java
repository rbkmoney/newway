package com.rbkmoney.newway.dao.withdrawal.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.withdrawal.iface.WithdrawalDao;
import com.rbkmoney.newway.domain.tables.pojos.Withdrawal;
import com.rbkmoney.newway.domain.tables.records.WithdrawalRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;

import static com.rbkmoney.newway.domain.tables.Wallet.WALLET;
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
    public Optional<Long> save(Withdrawal withdrawal) throws DaoException {
        WithdrawalRecord record = getDslContext().newRecord(WITHDRAWAL, withdrawal);
        Query query = getDslContext()
                .insertInto(WITHDRAWAL)
                .set(record)
                .onConflict(WITHDRAWAL.WITHDRAWAL_ID, WITHDRAWAL.SEQUENCE_ID)
                .doNothing()
                .returning(WITHDRAWAL.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public Withdrawal get(String withdrawalId) throws DaoException {
        Query query = getDslContext().selectFrom(WITHDRAWAL)
                .where(WITHDRAWAL.WITHDRAWAL_ID.eq(withdrawalId)
                        .and(WITHDRAWAL.CURRENT));
        return fetchOne(query, withdrawalRowMapper);
    }

    @Override
    public void updateNotCurrent(Long id) throws DaoException {
        Query query = getDslContext().update(WITHDRAWAL).set(WITHDRAWAL.CURRENT, false)
                .where(WITHDRAWAL.ID.eq(id)
                        .and(WITHDRAWAL.CURRENT));
        execute(query);
    }
}
