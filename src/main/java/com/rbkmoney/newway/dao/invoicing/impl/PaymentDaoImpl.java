package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.model.InvoicingKey;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.Collection;
import java.util.List;
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
    public void updateBatch(List<Payment> payments) throws DaoException{
        List<Query> queries = payments.stream()
                .map(payment -> getDslContext().newRecord(PAYMENT, payment))
                .map(paymentRecord -> getDslContext().update(PAYMENT)
                        .set(paymentRecord)
                        .where(PAYMENT.ID.eq(paymentRecord.getId())))
                .collect(Collectors.toList());
        batchExecute(queries);
    }

    @Override
    public Payment get(String invoiceId, String paymentId) throws DaoException {
        Query query = getDslContext().selectFrom(PAYMENT)
                .where(PAYMENT.INVOICE_ID.eq(invoiceId)
                        .and(PAYMENT.PAYMENT_ID.eq(paymentId))
                        .and(PAYMENT.CURRENT));

        return fetchOne(query, paymentRowMapper);
    }

    @Override
    public void switchCurrent(Collection<InvoicingKey> paymentsSwitchIds) throws DaoException {
        paymentsSwitchIds.forEach(ik ->
                this.getNamedParameterJdbcTemplate().update("update nw.payment set current = false where invoice_id =:invoice_id and payment_id=:payment_id and current;" +
                                "update nw.payment set current = true where id = (select max(id) from nw.payment where invoice_id =:invoice_id and payment_id=:payment_id);",
                        new MapSqlParameterSource("invoice_id", ik.getInvoiceId()).addValue("payment_id", ik.getPaymentId())));
    }
}
