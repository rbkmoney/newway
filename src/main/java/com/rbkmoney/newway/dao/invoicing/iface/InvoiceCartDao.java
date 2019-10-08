package com.rbkmoney.newway.dao.invoicing.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.InvoiceCart;
import com.rbkmoney.newway.exception.DaoException;

import java.util.List;

public interface InvoiceCartDao extends GenericDao {

    void save(List<InvoiceCart> invoiceCartList) throws DaoException;

    List<InvoiceCart> getByInvId(Long invId) throws DaoException;

}
