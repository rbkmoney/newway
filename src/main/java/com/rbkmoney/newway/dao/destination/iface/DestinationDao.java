package com.rbkmoney.newway.dao.destination.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Destination;
import com.rbkmoney.newway.exception.DaoException;

import java.util.Optional;

public interface DestinationDao extends GenericDao {

    Optional<Long> save(Destination destination) throws DaoException;

    Destination get(String destinationId) throws DaoException;

    void updateNotCurrent(Long destinationId) throws DaoException;

}
