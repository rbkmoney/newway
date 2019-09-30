package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceDao;
import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.domain.tables.records.InvoiceRecord;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.model.InvoicingSwitchKey;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.*;
import java.util.stream.Collectors;

import static com.rbkmoney.newway.domain.tables.Invoice.INVOICE;

@Component
public class InvoiceDaoImpl extends AbstractGenericDao implements InvoiceDao {

    private final RowMapper<Invoice> invoiceRowMapper;

    @Autowired
    public InvoiceDaoImpl(DataSource dataSource) {
        super(dataSource);
        invoiceRowMapper = new RecordRowMapper<>(INVOICE, Invoice.class);
    }

    @Override
    public Long save(Invoice invoice) throws DaoException {
        InvoiceRecord invoiceRecord = getDslContext().newRecord(INVOICE, invoice);
        Query query = getDslContext().insertInto(INVOICE)
                .set(invoiceRecord)
                .onConflict(INVOICE.INVOICE_ID, INVOICE.SEQUENCE_ID, INVOICE.CHANGE_ID)
                .doNothing()
                .returning(INVOICE.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue).orElse(null);
    }

    @Override
    public void saveBatch(List<Invoice> invoices) throws DaoException {
        List<Query> queries = invoices.stream()
                .map(invoice -> getDslContext().newRecord(INVOICE, invoice))
                .map(invoiceRecord -> getDslContext().insertInto(INVOICE)
                        .set(invoiceRecord)
                        .onConflict(INVOICE.INVOICE_ID, INVOICE.SEQUENCE_ID, INVOICE.CHANGE_ID)
                        .doNothing()
                )
                .collect(Collectors.toList());
        batchExecute(queries);
    }

    @Override
    public Invoice get(String invoiceId) throws DaoException {
        Query query = getDslContext().selectFrom(INVOICE)
                .where(INVOICE.INVOICE_ID.eq(invoiceId).and(INVOICE.CURRENT));
        return fetchOne(query, invoiceRowMapper);
    }

    @Override
    public void switchCurrent(List<InvoicingSwitchKey> invoicesSwitchIds) throws DaoException {
        List<Query> queries = invoicesSwitchIds.stream().map(s -> Arrays.asList(
                getDslContext().update(INVOICE).set(INVOICE.CURRENT, false).where(INVOICE.INVOICE_ID.eq(s.getInvoiceId())),
                getDslContext().update(INVOICE).set(INVOICE.CURRENT, true).where(INVOICE.ID.eq(s.getId())))
        ).flatMap(Collection::stream).collect(Collectors.toList());
        batchExecute(queries);
    }
}
