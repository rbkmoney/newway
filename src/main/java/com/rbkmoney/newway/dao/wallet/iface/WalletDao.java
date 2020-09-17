package com.rbkmoney.newway.dao.wallet.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Wallet;
import com.rbkmoney.newway.exception.DaoException;

import java.util.Optional;

public interface WalletDao extends GenericDao {

    Optional<Long> save(Wallet wallet) throws DaoException;

    Wallet get(String walletId) throws DaoException;

    void updateNotCurrent(Long id) throws DaoException;

}
