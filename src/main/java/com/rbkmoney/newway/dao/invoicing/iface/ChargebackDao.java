package com.rbkmoney.newway.dao.invoicing.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Chargeback;
import com.rbkmoney.newway.exception.DaoException;

public interface ChargebackDao extends GenericDao {

    Long save(Chargeback chargeback) throws DaoException;

    Chargeback get(String invoiceId, String paymentId, String chargebackId) throws DaoException;

    void updateNotCurrent(Long id) throws DaoException;

}
