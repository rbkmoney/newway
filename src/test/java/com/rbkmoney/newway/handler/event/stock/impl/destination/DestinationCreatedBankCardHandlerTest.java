package com.rbkmoney.newway.handler.event.stock.impl.destination;

import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.AbstractAppDaoTests;
import com.rbkmoney.newway.domain.tables.pojos.Destination;
import com.rbkmoney.newway.utils.DestinationHandlerTestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static com.rbkmoney.newway.domain.tables.Destination.DESTINATION;
import static io.github.benas.randombeans.api.EnhancedRandom.random;

public class DestinationCreatedBankCardHandlerTest extends AbstractAppDaoTests {

    @Autowired
    private DestinationCreatedHandler destinationCreatedHandler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    Destination destination = random(Destination.class);
    String sqlStatement = "select * from nw.destination LIMIT 1;";

    @Before
    public void setUp() {
        destination.setCurrent(true);
    }

    @Test
    public void destinationCreatedHandlerTest() {
        com.rbkmoney.fistful.base.Resource fistfulResource = new com.rbkmoney.fistful.base.Resource();
        fistfulResource.setBankCard(DestinationHandlerTestUtils.createResourceBankCard());
        com.rbkmoney.fistful.destination.Destination fistfulDestination
                = DestinationHandlerTestUtils.createFistfulDestination(fistfulResource);

        destinationCreatedHandler.handle(
                DestinationHandlerTestUtils.createCreated(fistfulDestination),
                DestinationHandlerTestUtils.createCreatedMachineEvent(destination.getDestinationId(), fistfulDestination));

        Destination destinationResult = jdbcTemplate.queryForObject(sqlStatement,
                new RecordRowMapper<>(DESTINATION, Destination.class));

        Assert.assertNotNull(destinationResult.getResourceBankCardBin());
        Assert.assertNotNull(destinationResult.getResourceBankCardMaskedPan());
        Assert.assertNotNull(destinationResult.getResourceBankCardToken());
    }

}