package com.rbkmoney.newway.dao.deposit_revert.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.DepositRevert;
import com.rbkmoney.newway.exception.DaoException;

import java.util.Optional;

public interface DepositRevertDao extends GenericDao {

    Optional<Long> save(DepositRevert revert) throws DaoException;

    DepositRevert get(String depositId, String revertId) throws DaoException;

    void updateNotCurrent(Long depositId) throws DaoException;

}
