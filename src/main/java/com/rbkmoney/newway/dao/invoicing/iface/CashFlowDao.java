package com.rbkmoney.newway.dao.invoicing.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.enums.AdjustmentCashFlowType;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.exception.DaoException;

import java.util.List;

public interface CashFlowDao extends GenericDao {

    void save(List<CashFlow> cashFlows) throws DaoException;

    List<CashFlow> getByObjId(Long objId, PaymentChangeType paymentchangetype) throws DaoException;

    List<CashFlow> getForAdjustments(Long adjId, AdjustmentCashFlowType adjustmentcashflowtype) throws DaoException;

}
