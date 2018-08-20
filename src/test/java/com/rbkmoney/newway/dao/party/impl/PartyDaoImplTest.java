package com.rbkmoney.newway.dao.party.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.domain.tables.pojos.Party;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.*;

public class PartyDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private PartyDao partyDao;

    @Test
    public void test() {
        Party party = random(Party.class);
        party.setCurrent(true);
        partyDao.save(party);
        Party partyGet = partyDao.get(party.getPartyId());
        assertEquals(party, partyGet);
        partyDao.update(party.getPartyId());
        assertNull(partyDao.get(party.getPartyId()));
        assertEquals(partyDao.getLastEventId(), party.getEventId());
    }
}