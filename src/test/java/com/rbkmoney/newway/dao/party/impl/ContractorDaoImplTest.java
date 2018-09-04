package com.rbkmoney.newway.dao.party.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.party.iface.ContractorDao;
import com.rbkmoney.newway.domain.tables.pojos.Contractor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.*;

public class ContractorDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private ContractorDao contractorDao;

    @Test
    public void test() {
        Contractor contractor = random(Contractor.class);
        contractor.setCurrent(true);
        contractorDao.save(contractor);
        Contractor contractorGet = contractorDao.get(contractor.getPartyId(), contractor.getContractorId());
        assertEquals(contractor, contractorGet);
        List<Contractor> contractors = contractorDao.getByPartyId(contractor.getPartyId());
        assertEquals(1, contractors.size());
        assertEquals(contractor, contractors.get(0));
        contractorDao.updateNotCurrent(contractor.getPartyId(), contractor.getContractorId());
        assertNull(contractorDao.get(contractor.getPartyId(), contractor.getContractorId()));
    }
}