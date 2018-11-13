package com.rbkmoney.newway.dao.destination.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.destination.iface.DestinationDao;
import com.rbkmoney.newway.domain.tables.pojos.Destination;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DestinationDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private DestinationDao destinationDao;

    @Test
    public void saveAndGetTest() {
        Destination destination = random(Destination.class);
        destination.setCurrent(true);
        Long id = destinationDao.save(destination);
        destination.setId(id);
        assertEquals(destination, destinationDao.get(destination.getDestinationId()));
        destinationDao.updateNotCurrent(destination.getDestinationId());
        assertNull(destinationDao.get(destination.getDestinationId()));
    }

}
