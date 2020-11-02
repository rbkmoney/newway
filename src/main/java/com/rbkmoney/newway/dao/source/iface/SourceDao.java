package com.rbkmoney.newway.dao.source.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Source;
import com.rbkmoney.newway.exception.DaoException;

import java.util.Optional;

public interface SourceDao extends GenericDao {

    Optional<Long> save(Source source) throws DaoException;

    Source get(String sourceId) throws DaoException;

    void updateNotCurrent(Long sourceId) throws DaoException;

}
