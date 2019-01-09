package com.rbkmoney.newway.dao.withdrawal_session.iface;

import com.rbkmoney.newway.dao.common.iface.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.WithdrawalSession;
import com.rbkmoney.newway.exception.DaoException;

public interface WithdrawalSessionDao extends GenericDao {

    Long getLastEventId() throws DaoException;

    Long save(WithdrawalSession withdrawalSession) throws DaoException;

    WithdrawalSession get(String sessionId) throws DaoException;

    void updateNotCurrent(String sessionId) throws DaoException;

}
