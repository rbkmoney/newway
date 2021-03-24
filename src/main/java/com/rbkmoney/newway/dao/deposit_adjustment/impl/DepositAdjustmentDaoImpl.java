package com.rbkmoney.newway.dao.deposit_adjustment.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.deposit_adjustment.iface.DepositAdjustmentDao;
import com.rbkmoney.newway.domain.tables.pojos.DepositAdjustment;
import com.rbkmoney.newway.domain.tables.records.DepositAdjustmentRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.Optional;

import static com.rbkmoney.newway.domain.tables.DepositAdjustment.DEPOSIT_ADJUSTMENT;

@Component
public class DepositAdjustmentDaoImpl extends AbstractGenericDao implements DepositAdjustmentDao {

    private final RowMapper<DepositAdjustment> depositRowMapper;

    @Autowired
    public DepositAdjustmentDaoImpl(DataSource dataSource) {
        super(dataSource);
        depositRowMapper = new RecordRowMapper<>(DEPOSIT_ADJUSTMENT, DepositAdjustment.class);
    }

    @Override
    public Optional<Long> save(DepositAdjustment adjustment) throws DaoException {
        DepositAdjustmentRecord record = getDslContext().newRecord(DEPOSIT_ADJUSTMENT, adjustment);
        Query query = getDslContext()
                .insertInto(DEPOSIT_ADJUSTMENT)
                .set(record)
                .onConflict(DEPOSIT_ADJUSTMENT.DEPOSIT_ID, DEPOSIT_ADJUSTMENT.ADJUSTMENT_ID, DEPOSIT_ADJUSTMENT.SEQUENCE_ID)
                .doNothing()
                .returning(DEPOSIT_ADJUSTMENT.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public DepositAdjustment get(String depositId, String adjustmentId) throws DaoException {
        Query query = getDslContext().selectFrom(DEPOSIT_ADJUSTMENT)
                .where(DEPOSIT_ADJUSTMENT.DEPOSIT_ID.eq(depositId)
                        .and(DEPOSIT_ADJUSTMENT.ADJUSTMENT_ID.eq(adjustmentId))
                        .and(DEPOSIT_ADJUSTMENT.CURRENT));
        return fetchOne(query, depositRowMapper);
    }

    @Override
    public void updateNotCurrent(Long adjustmentId) throws DaoException {
        Query query = getDslContext().update(DEPOSIT_ADJUSTMENT).set(DEPOSIT_ADJUSTMENT.CURRENT, false)
                .where(DEPOSIT_ADJUSTMENT.ID.eq(adjustmentId)
                        .and(DEPOSIT_ADJUSTMENT.CURRENT));
        execute(query);
    }

}
