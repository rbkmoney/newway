package com.rbkmoney.newway.dao.dominant.iface;

import com.rbkmoney.newway.dao.common.iface.GenericDao;
import com.rbkmoney.newway.exception.DaoException;

public interface DominantDao extends GenericDao {
    Long getLastVersionId() throws DaoException;
}
