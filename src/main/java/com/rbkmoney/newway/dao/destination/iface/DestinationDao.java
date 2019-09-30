package com.rbkmoney.newway.dao.destination.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Destination;
import com.rbkmoney.newway.exception.DaoException;

public interface DestinationDao extends GenericDao {

    Long getLastEventId() throws DaoException;

    Long save(Destination destination) throws DaoException;

    Destination get(String destinationId) throws DaoException;

    void updateNotCurrent(String destinationId) throws DaoException;

}
