package com.rbkmoney.newway.dao.deposit.revert.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.deposit.revert.iface.DepositRevertDao;
import com.rbkmoney.newway.domain.tables.pojos.DepositRevert;
import com.rbkmoney.newway.domain.tables.records.DepositRevertRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.Optional;

import static com.rbkmoney.newway.domain.tables.DepositRevert.DEPOSIT_REVERT;

@Component
public class DepositRevertDaoImpl extends AbstractGenericDao implements DepositRevertDao {

    private final RowMapper<DepositRevert> depositRowMapper;

    @Autowired
    public DepositRevertDaoImpl(DataSource dataSource) {
        super(dataSource);
        depositRowMapper = new RecordRowMapper<>(DEPOSIT_REVERT, DepositRevert.class);
    }

    @Override
    public Optional<Long> save(DepositRevert revert) throws DaoException {
        DepositRevertRecord record = getDslContext().newRecord(DEPOSIT_REVERT, revert);
        Query query = getDslContext()
                .insertInto(DEPOSIT_REVERT)
                .set(record)
                .onConflict(DEPOSIT_REVERT.DEPOSIT_ID, DEPOSIT_REVERT.REVERT_ID, DEPOSIT_REVERT.SEQUENCE_ID)
                .doNothing()
                .returning(DEPOSIT_REVERT.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public DepositRevert get(String depositId, String revertId) throws DaoException {
        Query query = getDslContext().selectFrom(DEPOSIT_REVERT)
                .where(DEPOSIT_REVERT.DEPOSIT_ID.eq(depositId)
                        .and(DEPOSIT_REVERT.REVERT_ID.eq(revertId))
                        .and(DEPOSIT_REVERT.CURRENT));
        return fetchOne(query, depositRowMapper);
    }

    @Override
    public void updateNotCurrent(Long revertId) throws DaoException {
        Query query = getDslContext().update(DEPOSIT_REVERT).set(DEPOSIT_REVERT.CURRENT, false)
                .where(DEPOSIT_REVERT.ID.eq(revertId)
                        .and(DEPOSIT_REVERT.CURRENT));
        execute(query);
    }

}
