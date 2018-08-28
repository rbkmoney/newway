package com.rbkmoney.newway.dao.party.iface;

import com.rbkmoney.newway.dao.common.iface.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Contract;
import com.rbkmoney.newway.exception.DaoException;

public interface ContractDao extends GenericDao {
    Long save(Contract contract) throws DaoException;
    Contract get(String contractId) throws DaoException;
    void updateNotCurrent(String contractId) throws DaoException;
}
