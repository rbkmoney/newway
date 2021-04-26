package com.rbkmoney.newway.service;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.newway.dao.AbstractAppDaoTests;
import com.rbkmoney.newway.domain.enums.PaymentToolType;
import com.rbkmoney.sink.common.serialization.impl.ThriftBinarySerializer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;

import static org.junit.Assert.*;

public class RecurrentPaymentToolServiceTest extends AbstractAppDaoTests {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private RecurrentPaymentToolService recurrentPaymentToolService;

    @Test
    public void handleEventsTest() {
        String recurrentId = "recurrentId";
        List<MachineEvent> events = buildEvent(recurrentId);
        recurrentPaymentToolService.handleEvents(events);

        String sql = "select * from nw.recurrent_payment_tool where recurrent_payment_tool_id = :id";
        List<com.rbkmoney.newway.domain.tables.pojos.RecurrentPaymentTool> recurrentPaymentTools =
                jdbcTemplate.query(sql, new MapSqlParameterSource("id", recurrentId),
                        new BeanPropertyRowMapper<>(
                                com.rbkmoney.newway.domain.tables.pojos.RecurrentPaymentTool.class));
        assertEquals(7, recurrentPaymentTools.size());

        var created = recurrentPaymentTools.get(0);
        assertEquals(recurrentId, created.getRecurrentPaymentToolId());
        assertEquals("shop_id", created.getShopId());
        assertEquals(com.rbkmoney.newway.domain.enums.RecurrentPaymentToolStatus.created, created.getStatus());
        assertEquals(PaymentToolType.bank_card, created.getPaymentToolType());
        assertEquals(123, created.getAmount().longValue());
        assertEquals("high", created.getRiskScore());
        assertEquals(54, created.getRouteProviderId().intValue());
        assertFalse(created.getCurrent());

        var riskScoreChanged = recurrentPaymentTools.get(1);
        assertEquals("fatal", riskScoreChanged.getRiskScore());
        assertNotEquals(created.getId(), riskScoreChanged.getId());
        assertFalse(riskScoreChanged.getCurrent());

        var routeChanged = recurrentPaymentTools.get(2);
        assertEquals(123, routeChanged.getRouteProviderId().longValue());
        assertEquals(456, routeChanged.getRouteTerminalId().longValue());
        assertFalse(routeChanged.getCurrent());

        var abandoned = recurrentPaymentTools.get(3);
        assertEquals(com.rbkmoney.newway.domain.enums.RecurrentPaymentToolStatus.abandoned, abandoned.getStatus());
        assertFalse(abandoned.getCurrent());

        var acquired = recurrentPaymentTools.get(4);
        assertEquals("kek_token", acquired.getRecToken());
        assertNotEquals(created.getRecToken(), acquired.getRecToken());
        assertFalse(acquired.getCurrent());

        var failed = recurrentPaymentTools.get(5);
        assertEquals(com.rbkmoney.newway.domain.enums.RecurrentPaymentToolStatus.failed, failed.getStatus());
        assertFalse(failed.getCurrent());

        var sessionChanged = recurrentPaymentTools.get(6);
        assertEquals("trxId", sessionChanged.getSessionPayloadTransactionBoundTrxId());
        assertEquals("rrn", sessionChanged.getSessionPayloadTransactionBoundTrxAdditionalInfoRrn());
        assertTrue(sessionChanged.getCurrent());

    }

    private List<MachineEvent> buildEvent(String recurrentId) {
        ThriftBinarySerializer<RecurrentPaymentToolEventData> serializer = new ThriftBinarySerializer<>();
        return Collections.singletonList(new MachineEvent()
                .setSourceId("")
                .setEventId(123L)
                .setCreatedAt("2016-03-22T06:12:27Z")
                .setSourceId(recurrentId)
                .setData(Value.bin(serializer.serialize(new RecurrentPaymentToolEventData().setChanges(
                        List.of(
                                RecurrentPaymentToolChange.rec_payment_tool_created(
                                        new RecurrentPaymentToolHasCreated()
                                                .setRecPaymentTool(new RecurrentPaymentTool()
                                                        .setId(recurrentId)
                                                        .setShopId("shop_id")
                                                        .setPartyId("party_id")
                                                        .setPartyRevision(124)
                                                        .setDomainRevision(1245)
                                                        .setStatus(RecurrentPaymentToolStatus
                                                                .created(new RecurrentPaymentToolCreated()))
                                                        .setCreatedAt("2016-03-22T06:12:27Z")
                                                        .setPaymentResource(new DisposablePaymentResource()
                                                                .setPaymentTool(PaymentTool.bank_card(new BankCard()
                                                                        .setToken("kkekekek_token")
                                                                        .setPaymentSystem(
                                                                                new PaymentSystemRef().setId("amex")
                                                                        )
                                                                        .setBin("bin")
                                                                        .setLastDigits("masked")
                                                                        .setTokenProviderDeprecated(
                                                                                LegacyBankCardTokenProvider.applepay)
                                                                        .setIssuerCountry(Residence.ABH)
                                                                        .setBankName("bank_name")
                                                                        .setMetadata(Map.of("kek",
                                                                                com.rbkmoney.damsel.msgpack.Value
                                                                                        .b(true)))))
                                                                .setPaymentSessionId("kek_session_id")
                                                                .setClientInfo(new ClientInfo()
                                                                        .setIpAddress("127.0.0.1")
                                                                        .setFingerprint("kekksiki")))
                                                        .setRecToken("kek_token_111")
                                                        .setRoute(new PaymentRoute()
                                                                .setProvider(new ProviderRef(888))
                                                                .setTerminal(new TerminalRef(9999)))
                                                        .setMinimalPaymentCost(new Cash(123, new CurrencyRef("RUB")))
                                                )
                                                .setRiskScore(RiskScore.high)
                                                .setRoute(new PaymentRoute()
                                                        .setProvider(new ProviderRef(54))
                                                        .setTerminal(new TerminalRef(9883)))
                                ),
                                RecurrentPaymentToolChange.rec_payment_tool_risk_score_changed(
                                        new RecurrentPaymentToolRiskScoreChanged()
                                                .setRiskScore(RiskScore.fatal)
                                ),
                                RecurrentPaymentToolChange.rec_payment_tool_route_changed(
                                        new RecurrentPaymentToolRouteChanged()
                                                .setRoute(new PaymentRoute()
                                                        .setProvider(new ProviderRef(123))
                                                        .setTerminal(new TerminalRef(456)))
                                ),
                                RecurrentPaymentToolChange.rec_payment_tool_abandoned(
                                        new RecurrentPaymentToolHasAbandoned()
                                ),
                                RecurrentPaymentToolChange.rec_payment_tool_acquired(
                                        new RecurrentPaymentToolHasAcquired()
                                                .setToken("kek_token")
                                ),
                                RecurrentPaymentToolChange.rec_payment_tool_failed(
                                        new RecurrentPaymentToolHasFailed()
                                                .setFailure(OperationFailure.failure(new Failure().setCode("code")))
                                ),
                                RecurrentPaymentToolChange.rec_payment_tool_session_changed(
                                        new RecurrentPaymentToolSessionChange()
                                                .setPayload(SessionChangePayload.session_transaction_bound(
                                                        new SessionTransactionBound()
                                                                .setTrx(new TransactionInfo()
                                                                        .setId("trxId")
                                                                        .setExtra(Map.of("lol", "kek"))
                                                                        .setAdditionalInfo(
                                                                                new AdditionalTransactionInfo()
                                                                                        .setRrn("rrn")))

                                                ))
                                )
                        ))))));
    }
}
