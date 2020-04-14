package com.rbkmoney.newway.dao.party.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Contractor;
import com.rbkmoney.newway.exception.DaoException;

import java.util.List;

public interface ContractorDao extends GenericDao {
    Long save(Contractor contractor) throws DaoException;
    void saveBatch(List<Contractor> contractor) throws DaoException;
    Contractor get(String partyId, String contractorId) throws DaoException;
    void updateNotCurrent(String partyId, String contractorId) throws DaoException;
    void updateNotCurrent(List<Long> ids) throws DaoException;
    List<Contractor> getByPartyId(String partyId);
}
