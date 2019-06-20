package com.rbkmoney.newway.dao.invoicing.iface;

import com.rbkmoney.newway.dao.common.iface.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.exception.DaoException;

public interface InvoiceDao extends GenericDao {

    Long save(Invoice invoice) throws DaoException;

    Invoice get(String invoiceId) throws DaoException;

    void updateNotCurrent(Long id) throws DaoException;
}
