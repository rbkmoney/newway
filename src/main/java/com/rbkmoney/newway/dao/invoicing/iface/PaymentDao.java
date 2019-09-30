package com.rbkmoney.newway.dao.invoicing.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.model.InvoicingSwitchKey;

import java.util.List;

public interface PaymentDao extends GenericDao {

    Long save(Payment payment) throws DaoException;

    void saveBatch(List<Payment> payments) throws DaoException;

    Payment get(String invoiceId, String paymentId) throws DaoException;

    void updateCommissions(List<Long> pmntIds) throws DaoException;

    void switchCurrent(List<InvoicingSwitchKey> invoicesSwitchIds) throws DaoException;
}
