package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.invoicing.iface.AdjustmentDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.Adjustment;
import com.rbkmoney.newway.domain.tables.records.AdjustmentRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

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
    public Long save(Adjustment adjustment) throws DaoException {
        AdjustmentRecord record = getDslContext().newRecord(ADJUSTMENT, adjustment);
        Query query = getDslContext().insertInto(ADJUSTMENT)
                .set(record)
                .onConflict(ADJUSTMENT.INVOICE_ID, ADJUSTMENT.SEQUENCE_ID, ADJUSTMENT.CHANGE_ID)
                .doNothing()
                .returning(ADJUSTMENT.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue).orElse(null);
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
    public void updateCommissions(Long adjId) throws DaoException {
        MapSqlParameterSource params = new MapSqlParameterSource("adjId", adjId).addValue("objType", PaymentChangeType.adjustment.name());
        this.getNamedParameterJdbcTemplate().update(
                "UPDATE nw.adjustment SET fee = (SELECT nw.get_adjustment_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = :adjId AND obj_type = CAST(:objType as nw.payment_change_type)), " +
                        "provider_fee = (SELECT nw.get_adjustment_provider_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = :adjId AND obj_type = CAST(:objType as nw.payment_change_type)), " +
                        "external_fee = (SELECT nw.get_adjustment_external_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = :adjId AND obj_type = CAST(:objType as nw.payment_change_type)) " +
                        "WHERE  id = :adjId",
                params);
    }

    @Override
    public void updateNotCurrent(Long id) throws DaoException {
        Query query = getDslContext().update(ADJUSTMENT).set(ADJUSTMENT.CURRENT, false).where(ADJUSTMENT.ID.eq(id));
        executeOne(query);
    }
}
