package com.rbkmoney.newway.dao.withdrawal.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Withdrawal;
import com.rbkmoney.newway.exception.DaoException;

import java.util.Optional;

public interface WithdrawalDao extends GenericDao {

    Optional<Long> save(Withdrawal withdrawal) throws DaoException;

    Withdrawal get(String withdrawalId) throws DaoException;

    void updateNotCurrent(Long withdrawalId) throws DaoException;

}
