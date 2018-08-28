package com.rbkmoney.newway.dao.party.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.party.iface.ContractAdjustmentDao;
import com.rbkmoney.newway.dao.party.iface.ContractDao;
import com.rbkmoney.newway.domain.tables.pojos.Contract;
import com.rbkmoney.newway.domain.tables.pojos.ContractAdjustment;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.Assert.*;

public class ContractAdjustmentDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private ContractAdjustmentDao contractAdjustmentDao;

    @Autowired
    private ContractDao contractDao;

    @Test
    public void test() {
        Contract contract = random(Contract.class);
        contract.setCurrent(true);
        Long cntrctId = contractDao.save(contract);
        List<ContractAdjustment> contractAdjustments = randomListOf(10, ContractAdjustment.class);
        contractAdjustments.forEach(ca -> ca.setCntrctId(cntrctId));
        contractAdjustmentDao.save(contractAdjustments);
        List<ContractAdjustment> byCntrctId = contractAdjustmentDao.getByCntrctId(cntrctId);
        assertEquals(new HashSet(contractAdjustments), new HashSet(byCntrctId));
    }
}