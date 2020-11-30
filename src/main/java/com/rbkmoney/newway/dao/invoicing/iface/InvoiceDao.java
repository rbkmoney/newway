package com.rbkmoney.newway.dao.invoicing.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.model.InvoicingKey;

import java.util.Collection;
import java.util.List;

public interface InvoiceDao extends GenericDao {

    void saveBatch(List<Invoice> invoices) throws DaoException;

    Invoice get(String invoiceId) throws DaoException;

    void switchCurrent(Collection<InvoicingKey> invoicesSwitchIds) throws DaoException;
}