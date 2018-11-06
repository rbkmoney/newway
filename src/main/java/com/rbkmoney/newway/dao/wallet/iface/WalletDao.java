package com.rbkmoney.newway.dao.wallet.iface;


import com.rbkmoney.newway.dao.common.iface.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Wallet;
import com.rbkmoney.newway.exception.DaoException;

public interface WalletDao extends GenericDao {

    Long getLastEventId() throws DaoException;

    Long save(Wallet wallet) throws DaoException;

    Wallet get(String walletId) throws DaoException;

    void updateNotCurrent(String walletId) throws DaoException;

}
