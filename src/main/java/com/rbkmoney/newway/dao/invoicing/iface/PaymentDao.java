package com.rbkmoney.newway.dao.invoicing.iface;

import com.rbkmoney.newway.dao.common.iface.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.exception.DaoException;

public interface PaymentDao extends GenericDao {

    Long save(Payment payment) throws DaoException;

    Payment get(String invoiceId, String paymentId) throws DaoException;

    void updateNotCurrent(String invoiceId, String paymentId) throws DaoException;
}
