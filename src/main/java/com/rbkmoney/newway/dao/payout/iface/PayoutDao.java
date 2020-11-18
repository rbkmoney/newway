package com.rbkmoney.newway.dao.payout.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Payout;
import com.rbkmoney.newway.exception.DaoException;

import java.util.Optional;

public interface PayoutDao extends GenericDao {

    Optional<Long> save(Payout payout) throws DaoException;

    Payout get(String payoutId) throws DaoException;

    void updateNotCurrent(Long id) throws DaoException;
}
