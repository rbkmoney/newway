package com.rbkmoney.newway.dao.invoicing.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.model.InvoicingKey;

import java.util.Collection;
import java.util.List;

public interface PaymentDao extends GenericDao {

    void saveBatch(List<Payment> payments) throws DaoException;

    void updateCurrentPayment(Payment payment);

    Payment get(String invoiceId, String paymentId) throws DaoException;

    void switchCurrent(Collection<InvoicingKey> invoicesSwitchIds) throws DaoException;
}
