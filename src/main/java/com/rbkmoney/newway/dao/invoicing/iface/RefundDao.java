package com.rbkmoney.newway.dao.invoicing.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Refund;
import com.rbkmoney.newway.exception.DaoException;

import java.util.Optional;

public interface RefundDao extends GenericDao {

    Optional<Long> save(Refund refund) throws DaoException;

    Refund get(String invoiceId, String paymentId, String refundId) throws DaoException;

    void updateCommissions(Long rfndId) throws DaoException;

    void updateNotCurrent(Long id) throws DaoException;
}
