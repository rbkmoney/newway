package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.invoicing.iface.RefundDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.Refund;
import com.rbkmoney.newway.domain.tables.records.RefundRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.Optional;

import static com.rbkmoney.newway.domain.tables.Refund.REFUND;

@Component
public class RefundDaoImpl extends AbstractGenericDao implements RefundDao {

    private final RowMapper<Refund> refundRowMapper;

    @Autowired
    public RefundDaoImpl(DataSource dataSource) {
        super(dataSource);
        refundRowMapper = new RecordRowMapper<>(REFUND, Refund.class);
    }

    @Override
    public Long save(Refund refund) throws DaoException {
        RefundRecord record = getDslContext().newRecord(REFUND, refund);
        Query query = getDslContext().insertInto(REFUND)
                .set(record)
                .onConflict(REFUND.INVOICE_ID, REFUND.SEQUENCE_ID, REFUND.CHANGE_ID)
                .doNothing()
                .returning(REFUND.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue).orElse(null);
    }

    @Override
    public Refund get(String invoiceId, String paymentId, String refundId) throws DaoException {
        Query query = getDslContext().selectFrom(REFUND)
                .where(REFUND.INVOICE_ID.eq(invoiceId)
                        .and(REFUND.PAYMENT_ID.eq(paymentId))
                        .and(REFUND.REFUND_ID.eq(refundId))
                        .and(REFUND.CURRENT));

        return fetchOne(query, refundRowMapper);
    }

    @Override
    public void updateCommissions(Long rfndId) throws DaoException {
        MapSqlParameterSource params = new MapSqlParameterSource("rfndId", rfndId).addValue("objType", PaymentChangeType.refund.name());
        this.getNamedParameterJdbcTemplate().update(
                "UPDATE nw.refund SET fee = (SELECT nw.get_refund_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = :rfndId AND obj_type = CAST(:objType as nw.payment_change_type)), " +
                        "provider_fee = (SELECT nw.get_refund_provider_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = :rfndId AND obj_type = CAST(:objType as nw.payment_change_type)), " +
                        "external_fee = (SELECT nw.get_refund_external_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = :rfndId AND obj_type = CAST(:objType as nw.payment_change_type)) " +
                        "WHERE  id = :rfndId",
                params);
    }

    @Override
    public void updateNotCurrent(Long id) throws DaoException {
        Query query = getDslContext().update(REFUND).set(REFUND.CURRENT, false).where(REFUND.ID.eq(id));
        executeOne(query);
    }
}
