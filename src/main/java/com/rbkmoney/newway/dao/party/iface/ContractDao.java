package com.rbkmoney.newway.dao.party.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Contract;
import com.rbkmoney.newway.exception.DaoException;

import java.util.List;
import java.util.Optional;

public interface ContractDao extends GenericDao {
    void saveBatch(List<Contract> contracts) throws DaoException;

    Optional<Long> save(Contract contract) throws DaoException;

    Contract get(String partyId, String contractId) throws DaoException;

    void updateNotCurrent(Long contractId) throws DaoException;

    void updateRevision(String partyId, long revision) throws DaoException;
}
