package com.rbkmoney.newway.dao.withdrawal.session.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.WithdrawalSession;
import com.rbkmoney.newway.exception.DaoException;

import java.util.Optional;

public interface WithdrawalSessionDao extends GenericDao {

    Optional<Long> save(WithdrawalSession withdrawalSession) throws DaoException;

    WithdrawalSession get(String sessionId) throws DaoException;

    void updateNotCurrent(Long sessionId) throws DaoException;

}
