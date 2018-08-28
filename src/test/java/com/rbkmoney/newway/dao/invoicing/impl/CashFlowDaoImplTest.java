package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.Assert.*;

public class CashFlowDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private CashFlowDao cashFlowDao;

    @Test
    public void test() {
        List<CashFlow> cashFlowList = randomListOf(10, CashFlow.class);
        cashFlowList.forEach(cf -> {
            cf.setObjId(123L);
            cf.setObjType(PaymentChangeType.payment);
        });
        cashFlowDao.save(cashFlowList);
        List<CashFlow> byObjId = cashFlowDao.getByObjId(123L, PaymentChangeType.payment);
        assertEquals(new HashSet(byObjId), new HashSet(byObjId));
    }
}