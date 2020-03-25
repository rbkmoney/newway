package com.rbkmoney.newway.dao.party.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Party;
import com.rbkmoney.newway.exception.DaoException;

import java.util.Optional;

public interface PartyDao extends GenericDao {
    Optional<Long> save(Party party) throws DaoException;

    Party get(String partyId) throws DaoException;

    void updateNotCurrent(Long partyId) throws DaoException;

    void saveWithUpdateCurrent(Party partySource, Long oldId, String eventName);
}
