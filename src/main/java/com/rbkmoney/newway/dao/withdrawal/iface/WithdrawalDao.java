package com.rbkmoney.newway.dao.withdrawal.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Withdrawal;
import com.rbkmoney.newway.exception.DaoException;

public interface WithdrawalDao extends GenericDao {

    Long getLastEventId() throws DaoException;

    Long save(Withdrawal withdrawal) throws DaoException;

    Withdrawal get(String withdrawalId) throws DaoException;

    void updateNotCurrent(String withdrawalId) throws DaoException;

}
