package com.rbkmoney.newway.dao.party.iface;

import com.rbkmoney.newway.dao.common.iface.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.ContractAdjustment;
import com.rbkmoney.newway.exception.DaoException;

import java.util.List;

public interface ContractAdjustmentDao extends GenericDao {
    void save(List<ContractAdjustment> contractAdjustmentList) throws DaoException;
    List<ContractAdjustment> getByCntrctId(Long cntrctId) throws DaoException;
}
