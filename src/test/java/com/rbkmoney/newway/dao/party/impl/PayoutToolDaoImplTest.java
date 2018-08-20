package com.rbkmoney.newway.dao.party.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.party.iface.ContractDao;
import com.rbkmoney.newway.dao.party.iface.PayoutToolDao;
import com.rbkmoney.newway.domain.tables.pojos.Contract;
import com.rbkmoney.newway.domain.tables.pojos.PayoutTool;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.Assert.*;

public class PayoutToolDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private PayoutToolDao payoutToolDao;

    @Autowired
    private ContractDao contractDao;

    @Test
    public void test() {
        Contract contract = random(Contract.class);
        contract.setCurrent(true);
        Long cntrctId = contractDao.save(contract);
        List<PayoutTool> payoutTools = randomListOf(10, PayoutTool.class);
        payoutTools.forEach(pt -> pt.setCntrctId(cntrctId));
        payoutToolDao.save(payoutTools);
        List<PayoutTool> byCntrctId = payoutToolDao.getByCntrctId(cntrctId);
        assertEquals(new HashSet(payoutTools), new HashSet(byCntrctId));
    }
}