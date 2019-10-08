package com.rbkmoney.newway.dao.deposit.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Deposit;
import com.rbkmoney.newway.exception.DaoException;

public interface DepositDao extends GenericDao {

    Long getLastEventId() throws DaoException;

    Long save(Deposit deposit) throws DaoException;

    Deposit get(String depositId) throws DaoException;

    void updateNotCurrent(String depositId) throws DaoException;

}
