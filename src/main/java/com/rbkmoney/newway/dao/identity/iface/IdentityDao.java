package com.rbkmoney.newway.dao.identity.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import com.rbkmoney.newway.exception.DaoException;

import java.util.Optional;

public interface IdentityDao extends GenericDao {

    Optional<Long> save(Identity identity) throws DaoException;

    Identity get(String identityId) throws DaoException;

    void updateNotCurrent(Long identityId) throws DaoException;

}
