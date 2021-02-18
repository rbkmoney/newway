package com.rbkmoney.newway.dao.deposit_adjustment.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.DepositAdjustment;
import com.rbkmoney.newway.exception.DaoException;

import java.util.Optional;

public interface DepositAdjustmentDao extends GenericDao {

    Optional<Long> save(DepositAdjustment deposit) throws DaoException;

    DepositAdjustment get(String depositId, String adjustmentId) throws DaoException;

    void updateNotCurrent(Long depositId) throws DaoException;

}
