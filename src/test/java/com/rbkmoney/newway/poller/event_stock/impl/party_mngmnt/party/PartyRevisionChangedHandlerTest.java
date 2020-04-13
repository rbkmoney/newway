package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.party;

import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.EventSource;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyRevisionChanged;
import com.rbkmoney.newway.dao.AbstractAppDaoTests;
import com.rbkmoney.newway.dao.party.iface.*;
import com.rbkmoney.newway.domain.tables.pojos.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.Assert.*;

@Slf4j
public class PartyRevisionChangedHandlerTest extends AbstractAppDaoTests {

    @Autowired
    private HikariDataSource dataSource;

    @Autowired
    private PartyRevisionChangedHandler partyRevisionChangedHandler;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private ShopDao shopDao;

    @Autowired
    private ContractorDao contractorDao;

    @Autowired
    private ContractAdjustmentDao contractAdjustmentDao;

    @Autowired
    private PayoutToolDao payoutToolDao;

    @Autowired
    private PartyDao partyDao;

    private static final int CNT = 500;

    private static final String PARTY_ID = "partyId";

    @Before
    public void setUp() throws Exception {

        log.info("setUp");

        Party party = random(Party.class, "id","current","wtime");
        party.setPartyId(PARTY_ID);
        partyDao.save(party);
        List<Contract> contracts = randomListOf(CNT, Contract.class, "current");
        List<ContractAdjustment> allAdjustments = new ArrayList<>();
        List<PayoutTool> allPayoutTools = new ArrayList<>();
        contracts.forEach(c -> {
            c.setPartyId(party.getPartyId());
            List<ContractAdjustment> adjustments = randomListOf(2, ContractAdjustment.class, "id");
            adjustments.forEach(ca -> ca.setCntrctId(c.getId()));
            allAdjustments.addAll(adjustments);
            List<PayoutTool> payoutTools = randomListOf(2, PayoutTool.class, "id");
            payoutTools.forEach(pt -> pt.setCntrctId(c.getId()));
            allPayoutTools.addAll(payoutTools);
        });
        contracts.forEach(c -> contractDao.save(c));
        contractAdjustmentDao.save(allAdjustments);
        payoutToolDao.save(allPayoutTools);

        List<Shop> shops = randomListOf(CNT, Shop.class, "id","current", "wtime");
        shops.forEach(s -> s.setPartyId(party.getPartyId()));
        shops.forEach(s -> shopDao.save(s));

        List<Contractor> contractors = randomListOf(CNT, Contractor.class, "id","current", "wtime");
        contractors.forEach(c -> c.setPartyId(party.getPartyId()));
        contractors.forEach(c -> contractorDao.save(c));

        log.info("All staff has been saved for partyId {}", party.getPartyId());

    }

    @Test
    public void testHandle() throws SQLException {
        PartyChange change = PartyChange.revision_changed(new PartyRevisionChanged()
                .setTimestamp("2016-03-22T06:12:27Z")
                .setRevision(1L));
        Event event = new Event()
                .setSource(EventSource.party_id(PARTY_ID))
                .setCreatedAt("2016-03-22T06:12:27Z");
        assertTrue(JdbcUtils.supportsBatchUpdates(dataSource.getConnection()));
        assertEquals("true", dataSource.getDataSourceProperties().get("reWriteBatchedInserts"));
        partyRevisionChangedHandler.handle(change, event, 1);
    }
}
