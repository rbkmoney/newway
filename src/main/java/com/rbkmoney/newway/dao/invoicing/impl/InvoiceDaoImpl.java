package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.common.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceDao;
import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.domain.tables.records.InvoiceRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.Tables.ADJUSTMENT;
import static com.rbkmoney.newway.domain.Tables.PAYMENT;
import static com.rbkmoney.newway.domain.tables.Invoice.INVOICE;
import static com.rbkmoney.newway.domain.tables.Refund.REFUND;

@Component
public class InvoiceDaoImpl extends AbstractGenericDao implements InvoiceDao {

    private final RowMapper<Invoice> invoiceRowMapper;

    @Autowired
    public InvoiceDaoImpl(DataSource dataSource) {
        super(dataSource);
        invoiceRowMapper = new RecordRowMapper<>(INVOICE, Invoice.class);
    }

    @Override
    public Long getLastEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(DSL.field("event_id"))).from(
                getDslContext().select(INVOICE.EVENT_ID.max().as("event_id")).from(INVOICE)
                        .unionAll(getDslContext().select(PAYMENT.EVENT_ID.max().as("event_id")).from(PAYMENT))
                        .unionAll(getDslContext().select(REFUND.EVENT_ID.max().as("event_id")).from(REFUND))
                        .unionAll(getDslContext().select(ADJUSTMENT.EVENT_ID.max().as("event_id")).from(ADJUSTMENT))
        );
        return fetchOne(query, Long.class);
    }

    @Override
    public Long save(Invoice invoice) throws DaoException {
        InvoiceRecord invoiceRecord = getDslContext().newRecord(INVOICE, invoice);
        Query query = getDslContext().insertInto(INVOICE).set(invoiceRecord).returning(INVOICE.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOneWithReturn(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Invoice get(String invoiceId) throws DaoException {
        Query query = getDslContext().selectFrom(INVOICE)
                .where(INVOICE.INVOICE_ID.eq(invoiceId).and(INVOICE.CURRENT));
        return fetchOne(query, invoiceRowMapper);
    }

    @Override
    public void updateNotCurrent(String invoiceId) throws DaoException {
        Query query = getDslContext().update(INVOICE).set(INVOICE.CURRENT, false)
                .where(INVOICE.INVOICE_ID.eq(invoiceId).and(INVOICE.CURRENT));
        executeOne(query);
    }
}
