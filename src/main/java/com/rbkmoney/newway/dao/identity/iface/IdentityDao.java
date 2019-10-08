package com.rbkmoney.newway.dao.identity.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import com.rbkmoney.newway.exception.DaoException;

public interface IdentityDao extends GenericDao {

    Long getLastEventId() throws DaoException;

    Long save(Identity identity) throws DaoException;

    Identity get(String identityId) throws DaoException;

    void updateNotCurrent(String identityId) throws DaoException;

}
