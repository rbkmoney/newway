package com.rbkmoney.newway.dao.payout.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Payout;
import com.rbkmoney.newway.exception.DaoException;

public interface PayoutDao extends GenericDao {

    Long getLastEventId() throws DaoException;

    Long save(Payout payout) throws DaoException;

    Payout get(String payoutId) throws DaoException;

    void updateNotCurrent(String payoutId) throws DaoException;
}
