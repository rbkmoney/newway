package com.rbkmoney.newway.dao.rate.iface;

import com.rbkmoney.newway.dao.common.iface.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Rate;
import com.rbkmoney.newway.exception.DaoException;

public interface RateDao extends GenericDao {

    Long getLastEventId() throws DaoException;

    Long save(Rate rate) throws DaoException;

    void updateNotCurrent(String sourceId) throws DaoException;
}
