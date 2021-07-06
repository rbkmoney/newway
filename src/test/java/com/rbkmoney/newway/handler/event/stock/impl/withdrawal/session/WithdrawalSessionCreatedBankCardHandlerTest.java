package com.rbkmoney.newway.handler.event.stock.impl.withdrawal.session;

import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.domain.tables.pojos.Destination;
import com.rbkmoney.newway.domain.tables.pojos.WithdrawalSession;
import com.rbkmoney.newway.kafka.AbstractKafkaTest;
import com.rbkmoney.newway.utils.WithdrawalSessionCreatedHandlerUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static com.rbkmoney.newway.domain.tables.WithdrawalSession.WITHDRAWAL_SESSION;
import static com.rbkmoney.newway.utils.WithdrawalSessionCreatedHandlerUtils.createSession;
import static io.github.benas.randombeans.api.EnhancedRandom.random;

public class WithdrawalSessionCreatedBankCardHandlerTest extends AbstractKafkaTest {

    @Autowired
    private WithdrawalSessionCreatedHandler withdrawalSessionCreatedHandler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    Destination destination = random(Destination.class);
    String sqlStatement = "select * from nw.withdrawal_session LIMIT 1;";

    @Before
    public void setUp() {
        destination.setCurrent(true);
    }

    @Test
    public void bankCardTest() {
        com.rbkmoney.fistful.base.Resource resource = new com.rbkmoney.fistful.base.Resource();
        resource.setBankCard(WithdrawalSessionCreatedHandlerUtils.createDestinationResourceBankCard());
        com.rbkmoney.fistful.withdrawal_session.Session session = createSession(resource);

        withdrawalSessionCreatedHandler.handle(
                WithdrawalSessionCreatedHandlerUtils.createCreated(session),
                WithdrawalSessionCreatedHandlerUtils.createCreatedMachineEvent(destination.getDestinationId(), session)
        );

        WithdrawalSession result = jdbcTemplate.queryForObject(sqlStatement,
                new RecordRowMapper<>(WITHDRAWAL_SESSION, WithdrawalSession.class));

        Assert.assertNotNull(result.getDestinationCardBin());
        Assert.assertNotNull(result.getDestinationCardMaskedPan());
        Assert.assertNotNull(result.getDestinationCardToken());
    }

}