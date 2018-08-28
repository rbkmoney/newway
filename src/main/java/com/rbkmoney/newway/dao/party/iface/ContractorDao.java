package com.rbkmoney.newway.dao.party.iface;

import com.rbkmoney.newway.dao.common.iface.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Contractor;
import com.rbkmoney.newway.exception.DaoException;

public interface ContractorDao extends GenericDao {
    Long save(Contractor contractor) throws DaoException;
    Contractor get(String contractorId) throws DaoException;
    void updateNotCurrent(String contractorId) throws DaoException;
}
