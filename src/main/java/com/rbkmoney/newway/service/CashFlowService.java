package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CashFlowService {
    private final CashFlowDao cashFlowDao;

    public CashFlowService(CashFlowDao cashFlowDao) {
        this.cashFlowDao = cashFlowDao;
    }

    public void save(Long objSourceId, Long objId, PaymentChangeType type) {
        if (objId != null) {
            List<CashFlow> cashFlows = cashFlowDao.getByObjId(objSourceId, type);
            cashFlows.forEach(pcf -> {
                pcf.setId(null);
                pcf.setObjId(objId);
            });
            cashFlowDao.save(cashFlows);
        }
    }
}
