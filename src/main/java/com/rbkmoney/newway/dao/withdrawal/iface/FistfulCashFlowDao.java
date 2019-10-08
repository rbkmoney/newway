package com.rbkmoney.newway.dao.withdrawal.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.newway.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.newway.exception.DaoException;

import java.util.List;

public interface FistfulCashFlowDao extends GenericDao {

    void save(List<FistfulCashFlow> fistfulCashFlowList) throws DaoException;

    List<FistfulCashFlow> getByObjId(Long objId, FistfulCashFlowChangeType cashFlowChangeType) throws DaoException;

}
