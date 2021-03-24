package com.rbkmoney.newway.dao.party.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.exception.DaoException;

public interface RevisionDao extends GenericDao {

    void saveShopsRevision(String partyId, long revision) throws DaoException;

    void saveContractsRevision(String partyId, long revision) throws DaoException;

    void saveContractorsRevision(String partyId, long revision) throws DaoException;
}
