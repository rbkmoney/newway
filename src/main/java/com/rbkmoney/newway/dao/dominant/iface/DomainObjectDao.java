package com.rbkmoney.newway.dao.dominant.iface;

import com.rbkmoney.newway.dao.common.iface.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Category;
import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.exception.DaoException;

public interface DomainObjectDao<T, I> extends GenericDao {

    Long save(T domainObject) throws DaoException;

    void updateNotCurrent(I objectId) throws DaoException;
}
