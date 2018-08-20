package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.common.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceCartDao;
import com.rbkmoney.newway.domain.tables.pojos.InvoiceCart;
import com.rbkmoney.newway.domain.tables.records.InvoiceCartRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

import static com.rbkmoney.newway.domain.tables.InvoiceCart.INVOICE_CART;

@Component
public class InvoiceCartDaoImpl extends AbstractGenericDao implements InvoiceCartDao {

    private final RowMapper<InvoiceCart> invoiceCartRowMapper;

    @Autowired
    public InvoiceCartDaoImpl(DataSource dataSource) {
        super(dataSource);
        invoiceCartRowMapper = new RecordRowMapper<>(INVOICE_CART, InvoiceCart.class);
    }

    @Override
    public void save(List<InvoiceCart> invoiceCartList) throws DaoException {
        //todo: Batch insert
        for (InvoiceCart invoiceCart : invoiceCartList) {
            InvoiceCartRecord record = getDslContext().newRecord(INVOICE_CART, invoiceCart);
            Query query = getDslContext().insertInto(INVOICE_CART).set(record);
            executeOne(query);
        }
    }

    @Override
    public List<InvoiceCart> getByInvId(Long invId) throws DaoException {
        Query query = getDslContext().selectFrom(INVOICE_CART)
                .where(INVOICE_CART.INV_ID.eq(invId));
        return fetch(query, invoiceCartRowMapper);
    }
}
