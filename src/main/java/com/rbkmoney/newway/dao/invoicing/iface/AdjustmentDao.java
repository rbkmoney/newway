package com.rbkmoney.newway.dao.invoicing.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Adjustment;
import com.rbkmoney.newway.exception.DaoException;

public interface AdjustmentDao extends GenericDao {

    Long save(Adjustment adjustment) throws DaoException;

    Adjustment get(String invoiceId, String paymentId, String adjustmentId) throws DaoException;

    void updateCommissions(Long adjId) throws DaoException;

    void updateNotCurrent(Long id) throws DaoException;

}
