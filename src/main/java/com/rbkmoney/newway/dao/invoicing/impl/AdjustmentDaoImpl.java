package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.invoicing.iface.AdjustmentDao;
import com.rbkmoney.newway.domain.tables.pojos.Adjustment;
import com.rbkmoney.newway.domain.tables.records.AdjustmentRecord;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.exception.NotFoundException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import java.util.Optional;

import static com.rbkmoney.newway.domain.Tables.ADJUSTMENT;

@Component
public class AdjustmentDaoImpl extends AbstractGenericDao implements AdjustmentDao {

    private final RowMapper<Adjustment> adjustmentRowMapper;

    @Autowired
    public AdjustmentDaoImpl(DataSource dataSource) {
        super(dataSource);
        adjustmentRowMapper = new RecordRowMapper<>(ADJUSTMENT, Adjustment.class);
    }

    @Override
    public Optional<Long> save(Adjustment adjustment) throws DaoException {
        AdjustmentRecord record = getDslContext().newRecord(ADJUSTMENT, adjustment);
        Query query = getDslContext().insertInto(ADJUSTMENT)
                .set(record)
                .onConflict(ADJUSTMENT.INVOICE_ID, ADJUSTMENT.SEQUENCE_ID, ADJUSTMENT.CHANGE_ID)
                .doNothing()
                .returning(ADJUSTMENT.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @NotNull
    @Override
    public Adjustment get(String invoiceId, String paymentId, String adjustmentId) throws DaoException {
        Query query = getDslContext().selectFrom(ADJUSTMENT)
                .where(ADJUSTMENT.INVOICE_ID.eq(invoiceId)
                        .and(ADJUSTMENT.PAYMENT_ID.eq(paymentId))
                        .and(ADJUSTMENT.ADJUSTMENT_ID.eq(adjustmentId))
                        .and(ADJUSTMENT.CURRENT));

        return Optional.ofNullable(fetchOne(query, adjustmentRowMapper))
                .orElseThrow(() -> new NotFoundException(
                        String.format("Adjustment not found, invoiceId='%s', paymentId='%s', adjustmentId='%s'",
                                invoiceId, paymentId, adjustmentId)));
    }

    @Override
    public void updateNotCurrent(Long id) throws DaoException {
        Query query = getDslContext().update(ADJUSTMENT).set(ADJUSTMENT.CURRENT, false).where(ADJUSTMENT.ID.eq(id));
        executeOne(query);
    }
}
