package com.rbkmoney.newway.dao.source.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Source;
import com.rbkmoney.newway.exception.DaoException;

public interface SourceDao extends GenericDao {

    Long getLastEventId() throws DaoException;

    Long save(Source source) throws DaoException;

    Source get(String sourceId) throws DaoException;

    void updateNotCurrent(String sourceId) throws DaoException;

}
