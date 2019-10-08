package com.rbkmoney.newway.dao.dominant.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.exception.DaoException;

public interface DomainObjectDao<T, I> extends GenericDao {

    Long save(T domainObject) throws DaoException;

    void updateNotCurrent(I objectId) throws DaoException;
}
