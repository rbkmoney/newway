package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceCartDao;
import com.rbkmoney.newway.domain.tables.pojos.InvoiceCart;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.List;
import java.util.stream.Collectors;

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
    public void save(List<InvoiceCart> carts) throws DaoException {
        List<Query> queries = carts.stream()
                .map(cart -> getDslContext().newRecord(INVOICE_CART, cart))
                .map(cartRecord -> getDslContext().insertInto(INVOICE_CART).set(cartRecord))
                .collect(Collectors.toList());
        batchExecute(queries);
    }

    @Override
    public List<InvoiceCart> getByInvId(Long invId) throws DaoException {
        Query query = getDslContext().selectFrom(INVOICE_CART)
                .where(INVOICE_CART.INV_ID.eq(invId));
        return fetch(query, invoiceCartRowMapper);
    }
}
