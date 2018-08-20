package com.rbkmoney.newway.dao.invoicing.iface;

import com.rbkmoney.newway.dao.common.iface.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Adjustment;
import com.rbkmoney.newway.exception.DaoException;

public interface AdjustmentDao extends GenericDao {

    Long save(Adjustment adjustment) throws DaoException;

    Adjustment get(String invoiceId, String paymentId, String adjustmentId) throws DaoException;

    void update(String invoiceId, String paymentId, String adjustmentId) throws DaoException;

}
