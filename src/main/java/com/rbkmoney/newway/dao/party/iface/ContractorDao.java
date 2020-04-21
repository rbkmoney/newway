package com.rbkmoney.newway.dao.party.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Contractor;
import com.rbkmoney.newway.exception.DaoException;

import java.util.List;
import java.util.Optional;

public interface ContractorDao extends GenericDao {
    void saveBatch(List<Contractor> contractor) throws DaoException;

    Optional<Long> save(Contractor contractor) throws DaoException;

    Contractor get(String partyId, String contractorId) throws DaoException;

    void updateNotCurrent(Long id) throws DaoException;

    void updateRevision(String partyId, long revision) throws DaoException;
}
