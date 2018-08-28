package com.rbkmoney.newway.dao.party.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.party.iface.ContractDao;
import com.rbkmoney.newway.domain.tables.pojos.Contract;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.*;

public class ContractDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private ContractDao contractDao;

    @Test
    public void test() {
        Contract contract = random(Contract.class);
        contract.setCurrent(true);
        contractDao.save(contract);
        Contract contractGet = contractDao.get(contract.getContractId());
        assertEquals(contract, contractGet);
        contractDao.updateNotCurrent(contract.getContractId());
        assertNull(contractDao.get(contract.getContractId()));
    }
}