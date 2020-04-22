package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.party;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyRevisionChanged;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.AbstractAppDaoTests;
import com.rbkmoney.newway.dao.party.iface.*;
import com.rbkmoney.newway.domain.tables.pojos.*;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
public class PartyRevisionChangedHandlerTest extends AbstractAppDaoTests {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

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
    @Mock
    private MachineEventParser eventParser;

    private static final int CNT = 100;

    private static final String PARTY_ID = "partyId";

    @Before
    public void setUp() throws Exception {
        log.info("setUp");
        Party party = random(Party.class, "id", "current", "wtime");
        party.setPartyId(PARTY_ID);
        partyDao.save(party);
        List<Contract> contracts = randomListOf(CNT, Contract.class, "current");
        List<ContractAdjustment> allAdjustments = new ArrayList<>();
        List<PayoutTool> allPayoutTools = new ArrayList<>();
        contracts.forEach(c -> {
            c.setContractId(UUID.randomUUID().toString());
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

        List<Shop> shops = randomListOf(CNT, Shop.class, "id", "current", "wtime");
        shops.forEach(s -> {
            s.setShopId(UUID.randomUUID().toString());
            s.setPartyId(party.getPartyId());
        });
        shops.forEach(s -> shopDao.save(s));

        List<Contractor> contractors = randomListOf(CNT, Contractor.class, "id", "current", "wtime");
        contractors.forEach(c -> {
            c.setPartyId(party.getPartyId());
            c.setContractorId(UUID.randomUUID().toString());
        });
        contractors.forEach(c -> contractorDao.save(c));

        log.info("All staff has been saved for partyId={}", party.getPartyId());

    }

    @Test
    public void testPerfomanceHandle() throws SQLException {
        PartyChange change = PartyChange.revision_changed(new PartyRevisionChanged()
                .setTimestamp("2016-03-22T06:12:27Z")
                .setRevision(1L));
        MachineEvent message = new MachineEvent()
                .setSourceId(PARTY_ID)
                .setCreatedAt("2016-03-22T06:12:27Z");

        EventPayload payload = new EventPayload();
        payload.setPartyChanges(List.of(change));

        Mockito.when(eventParser.parse(message)).thenReturn(payload);
        partyRevisionChangedHandler.handle(change, message, 1);

        assertEquals(Integer.valueOf(CNT), jdbcTemplate.queryForObject("select count(1) from nw.shop_revision", new MapSqlParameterSource(), Integer.class));
        assertEquals(Integer.valueOf(CNT), jdbcTemplate.queryForObject("select count(1) from nw.contract_revision", new MapSqlParameterSource(), Integer.class));
        assertEquals(Integer.valueOf(CNT), jdbcTemplate.queryForObject("select count(1) from nw.contractor_revision", new MapSqlParameterSource(), Integer.class));

    }
}
