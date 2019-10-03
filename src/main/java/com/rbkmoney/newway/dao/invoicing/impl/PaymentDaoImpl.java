package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.domain.tables.records.PaymentRecord;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.model.InvoicingSwitchKey;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.rbkmoney.newway.domain.Tables.PAYMENT;

@Component
public class PaymentDaoImpl extends AbstractGenericDao implements PaymentDao {

    private final RowMapper<Payment> paymentRowMapper;

    @Autowired
    public PaymentDaoImpl(DataSource dataSource) {
        super(dataSource);
        paymentRowMapper = new RecordRowMapper<>(PAYMENT, Payment.class);
    }

    @Override
    public void saveBatch(List<Payment> payments) throws DaoException {
        List<Query> queries = payments.stream()
                .map(payment -> getDslContext().newRecord(PAYMENT, payment))
                .map(paymentRecord -> getDslContext().insertInto(PAYMENT)
                        .set(paymentRecord)
                        .onConflict(PAYMENT.INVOICE_ID, PAYMENT.SEQUENCE_ID, PAYMENT.CHANGE_ID)
                        .doNothing()
                )
                .collect(Collectors.toList());
        batchExecute(queries);
    }

    @Override
    public void updateCommissions(List<Long> pmntIds) throws DaoException {
        MapSqlParameterSource[] params = pmntIds.stream().map(pmntId -> new MapSqlParameterSource("pmntId", pmntId)).toArray(MapSqlParameterSource[]::new);
        this.getNamedParameterJdbcTemplate().batchUpdate(
                "UPDATE nw.payment SET fee = (SELECT nw.get_payment_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = :pmntId AND obj_type = 'payment'), " +
                        "provider_fee = (SELECT nw.get_payment_provider_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = :pmntId AND obj_type = 'payment'), " +
                        "external_fee = (SELECT nw.get_payment_external_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = :pmntId AND obj_type = 'payment'), " +
                        "guarantee_deposit = (SELECT nw.get_payment_guarantee_deposit(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = :pmntId AND obj_type = 'payment') " +
                        "WHERE  id = :pmntId",
                params);
    }

    @Override
    public Payment get(String invoiceId, String paymentId) throws DaoException {
        Query query = getDslContext().selectFrom(PAYMENT)
                .where(PAYMENT.INVOICE_ID.eq(invoiceId).and(PAYMENT.PAYMENT_ID.eq(paymentId)).and(PAYMENT.CURRENT));

        return fetchOne(query, paymentRowMapper);
    }

    @Override
    public void switchCurrent(List<InvoicingSwitchKey> paymentsSwitchIds) throws DaoException {
        paymentsSwitchIds.forEach(s -> {
            execute(getDslContext().update(PAYMENT).set(PAYMENT.CURRENT, false).where(PAYMENT.INVOICE_ID.eq(s.getInvoiceId()).and(PAYMENT.PAYMENT_ID.eq(s.getPaymentId())).and(PAYMENT.CURRENT)));
            execute(getDslContext().update(PAYMENT).set(PAYMENT.CURRENT, true).where(PAYMENT.ID.eq(s.getId())));
        });
    }
}
