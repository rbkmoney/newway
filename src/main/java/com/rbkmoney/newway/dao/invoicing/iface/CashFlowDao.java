package com.rbkmoney.newway.dao.invoicing.iface;

import com.rbkmoney.newway.dao.common.iface.GenericDao;
import com.rbkmoney.newway.domain.enums.Adjustmentcashflowtype;
import com.rbkmoney.newway.domain.enums.Paymentchangetype;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.exception.DaoException;

import java.util.List;

public interface CashFlowDao extends GenericDao {

    void save(List<CashFlow> cashFlowList) throws DaoException;

    List<CashFlow> getByObjId(Long objId, Paymentchangetype paymentchangetype) throws DaoException;

    List<CashFlow> getForAdjustments(Long adjId, Adjustmentcashflowtype adjustmentcashflowtype) throws DaoException;

}
