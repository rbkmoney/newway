package com.rbkmoney.newway.dao.party.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Party;
import com.rbkmoney.newway.exception.DaoException;

public interface PartyDao extends GenericDao {
    Long getLastEventId() throws DaoException;
    Long save(Party party) throws DaoException;
    Party get(String partyId) throws DaoException;
    void updateNotCurrent(String partyId) throws DaoException;
}
