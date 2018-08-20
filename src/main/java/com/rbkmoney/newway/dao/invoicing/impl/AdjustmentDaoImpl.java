package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.common.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.invoicing.iface.AdjustmentDao;
import com.rbkmoney.newway.domain.tables.pojos.Adjustment;
import com.rbkmoney.newway.domain.tables.records.AdjustmentRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

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
    public Long save(Adjustment adjustment) throws DaoException {
        AdjustmentRecord record = getDslContext().newRecord(ADJUSTMENT, adjustment);
        Query query = getDslContext().insertInto(ADJUSTMENT).set(record).returning(ADJUSTMENT.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOneWithReturn(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Adjustment get(String invoiceId, String paymentId, String adjustmentId) throws DaoException {
        Query query = getDslContext().selectFrom(ADJUSTMENT)
                .where(ADJUSTMENT.INVOICE_ID.eq(invoiceId)
                        .and(ADJUSTMENT.PAYMENT_ID.eq(paymentId))
                        .and(ADJUSTMENT.ADJUSTMENT_ID.eq(adjustmentId))
                        .and(ADJUSTMENT.CURRENT));

        return fetchOne(query, adjustmentRowMapper);
    }

    @Override
    public void update(String invoiceId, String paymentId, String adjustmentId) throws DaoException {
        Query query = getDslContext().update(ADJUSTMENT).set(ADJUSTMENT.CURRENT, false)
                .where(ADJUSTMENT.INVOICE_ID.eq(invoiceId)
                        .and(ADJUSTMENT.PAYMENT_ID.eq(paymentId)
                        .and(ADJUSTMENT.ADJUSTMENT_ID.eq(adjustmentId))
                        .and(ADJUSTMENT.CURRENT)));
        executeOne(query);
    }
}
