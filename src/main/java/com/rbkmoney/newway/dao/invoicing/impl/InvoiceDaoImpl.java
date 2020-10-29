package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.invoicing.iface.BatchDao;
import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.model.InvoicingKey;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.*;
import java.util.stream.Collectors;

import static com.rbkmoney.newway.domain.tables.Invoice.INVOICE;

@Component
public class InvoiceDaoImpl extends AbstractGenericDao implements BatchDao<Invoice> {

    private final RowMapper<Invoice> invoiceRowMapper;

    @Autowired
    public InvoiceDaoImpl(DataSource dataSource) {
        super(dataSource);
        invoiceRowMapper = new RecordRowMapper<>(INVOICE, Invoice.class);
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
    public void updateBatch(List<Invoice> invoices) throws DaoException{
        List<Query> queries = invoices.stream()
                .map(invoice -> getDslContext().newRecord(INVOICE, invoice))
                .map(invoiceRecord -> getDslContext().update(INVOICE)
                        .set(invoiceRecord)
                        .where(INVOICE.ID.eq(invoiceRecord.getId())))
                .collect(Collectors.toList());
        batchExecute(queries);
    }

    @Override
    public Invoice get(InvoicingKey key) throws DaoException {
        Query query = getDslContext().selectFrom(INVOICE)
                .where(INVOICE.INVOICE_ID.eq(key.getInvoiceId()).and(INVOICE.CURRENT));
        return fetchOne(query, invoiceRowMapper);
    }

    @Override
    public void switchCurrent(Collection<InvoicingKey> invoicesSwitchIds) throws DaoException {
        invoicesSwitchIds.forEach(ik ->
                this.getNamedParameterJdbcTemplate().update("update nw.invoice set current = false where invoice_id =:invoice_id and current;" +
                                "update nw.invoice set current = true where id = (select max(id) from nw.invoice where invoice_id =:invoice_id);",
                        new MapSqlParameterSource("invoice_id", ik.getInvoiceId())));
    }
}
