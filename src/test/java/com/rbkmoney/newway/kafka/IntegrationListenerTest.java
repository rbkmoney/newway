package com.rbkmoney.newway.kafka;

import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.AbstractAppDaoTests;
import com.rbkmoney.newway.domain.tables.*;
import com.rbkmoney.newway.domain.tables.pojos.Destination;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.rbkmoney.newway.domain.tables.Deposit.DEPOSIT;
import static com.rbkmoney.newway.domain.tables.Destination.DESTINATION;
import static com.rbkmoney.newway.domain.tables.Identity.IDENTITY;
import static com.rbkmoney.newway.domain.tables.Payout.PAYOUT;
import static com.rbkmoney.newway.domain.tables.Source.SOURCE;
import static com.rbkmoney.newway.domain.tables.Wallet.WALLET;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@Ignore
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "kafka.ssl.enabled=true",
        "info.single-instance-mode=false"})
@ContextConfiguration(classes = {KafkaConfigTest.class})
public class IntegrationListenerTest extends AbstractAppDaoTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<Destination> destinationRowMapper = new RecordRowMapper<>(DESTINATION, Destination.class);
    private RowMapper<Deposit> depositRowMapper = new RecordRowMapper<>(DEPOSIT, Deposit.class);
    private RowMapper<Identity> identityRowMapper = new RecordRowMapper<>(IDENTITY, Identity.class);
    private RowMapper<Source> sourceRowMapper = new RecordRowMapper<>(SOURCE, Source.class);
    private RowMapper<Wallet> walletRowMapper = new RecordRowMapper<>(WALLET, Wallet.class);
    private RowMapper<Payout> payoutRowMapper = new RecordRowMapper<>(PAYOUT, Payout.class);

    @Test
    public void listenEmptyChanges() throws InterruptedException {
        Thread.sleep(20000L);

        List<Destination> query = jdbcTemplate.query("select * from nw.destination", destinationRowMapper);
        System.out.println("destination=" + query);

        List<Deposit> deposits = jdbcTemplate.query("select * from nw.deposit", depositRowMapper);
        System.out.println("deposit=" + deposits);

        List<Identity> identities = jdbcTemplate.query("select * from nw.identity", identityRowMapper);
        System.out.println("identity=" + identities);

        List<Source> sources = jdbcTemplate.query("select * from nw.source", sourceRowMapper);
        System.out.println("source=" + sources);

        List<Wallet> wallets = jdbcTemplate.query("select * from nw.wallet", walletRowMapper);
        System.out.println("wallet=" + wallets);

        List<Payout> payouts = jdbcTemplate.query("select * from nw.payout", payoutRowMapper);
        System.out.println("payout=" + payouts);
    }

}
