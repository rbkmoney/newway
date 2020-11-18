package com.rbkmoney.newway.dao.deposit.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Deposit;
import com.rbkmoney.newway.exception.DaoException;

import java.util.Optional;

public interface DepositDao extends GenericDao {

    Optional<Long> save(Deposit deposit) throws DaoException;

    Deposit get(String depositId) throws DaoException;

    void updateNotCurrent(Long depositId) throws DaoException;

}
