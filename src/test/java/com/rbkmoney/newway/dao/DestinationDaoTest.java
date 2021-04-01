package com.rbkmoney.newway.dao;

import com.rbkmoney.newway.dao.destination.iface.DestinationDao;
import com.rbkmoney.newway.domain.tables.pojos.Destination;
import com.rbkmoney.newway.exception.NotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

public class DestinationDaoTest extends AbstractAppDaoTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DestinationDao destinationDao;

    @Test(expected = NotFoundException.class)
    public void destinationDaoTest() {
        jdbcTemplate.execute("truncate table nw.destination cascade");
        Destination destination = random(Destination.class);
        destination.setCurrent(true);
        Long id = destinationDao.save(destination).get();
        destination.setId(id);
        Destination actual = destinationDao.get(destination.getDestinationId());
        assertEquals(destination, actual);
        destinationDao.updateNotCurrent(actual.getId());

        //check duplicate not error
        destinationDao.save(destination);

        destinationDao.get(destination.getDestinationId());
    }

}
