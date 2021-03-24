package com.rbkmoney.newway;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.newway.dao.AbstractAppDaoTests;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.domain.enums.PaymentStatus;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.service.InvoicingService;
import com.rbkmoney.newway.utils.MockUtils;
import com.rbkmoney.sink.common.serialization.impl.PaymentEventPayloadSerializer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class IntegrationTest extends AbstractAppDaoTests {

    @Autowired
    private InvoicingService invoicingService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PaymentDao paymentDao;

    @Test
    public void test() {
        PaymentEventPayloadSerializer serializer = new PaymentEventPayloadSerializer();
        String invoiceId = "inv_id";
        String paymentId = "1";
        List<MachineEvent> machineEventsFirst = List.of(
                new MachineEvent().setSourceId(invoiceId)
                        .setEventId(1)
                        .setCreatedAt(TypeUtil.temporalToString(LocalDateTime.now()))
                        .setData(Value.bin(serializer.serialize(
                                EventPayload.invoice_changes(
                                        List.of(InvoiceChange.invoice_created(new InvoiceCreated()
                                                        .setInvoice(MockUtils.buildInvoice(invoiceId))),
                                                InvoiceChange.invoice_status_changed(new InvoiceStatusChanged()
                                                        .setStatus(InvoiceStatus.fulfilled(new InvoiceFulfilled("keks")))),
                                                InvoiceChange.invoice_payment_change(new InvoicePaymentChange()
                                                        .setId(paymentId)
                                                        .setPayload(InvoicePaymentChangePayload.invoice_payment_started(new InvoicePaymentStarted()
                                                                .setPayment(MockUtils.buildPayment(paymentId))))),
                                                InvoiceChange.invoice_payment_change(new InvoicePaymentChange()
                                                        .setId(paymentId)
                                                        .setPayload(InvoicePaymentChangePayload.invoice_payment_cash_flow_changed(
                                                                new InvoicePaymentCashFlowChanged(
                                                                        List.of(new FinalCashFlowPosting()
                                                                                .setSource(new FinalCashFlowAccount(
                                                                                        CashFlowAccount.system(SystemCashFlowAccount.settlement), 1))
                                                                                .setDestination(new FinalCashFlowAccount(
                                                                                        CashFlowAccount.system(SystemCashFlowAccount.settlement), 1))
                                                                                .setVolume(new Cash(1, new CurrencyRef("RUB")))))
                                                        ))),
                                                InvoiceChange.invoice_payment_change(new InvoicePaymentChange()
                                                        .setId(paymentId)
                                                        .setPayload(InvoicePaymentChangePayload.invoice_payment_risk_score_changed(
                                                                new InvoicePaymentRiskScoreChanged(RiskScore.high)))),
                                                InvoiceChange.invoice_payment_change(new InvoicePaymentChange()
                                                        .setId(paymentId)
                                                        .setPayload(InvoicePaymentChangePayload.invoice_payment_status_changed(
                                                                new InvoicePaymentStatusChanged().setStatus(InvoicePaymentStatus.captured(
                                                                        new InvoicePaymentCaptured()))
                                                        ))))))))
        );
        invoicingService.handleEvents(machineEventsFirst);
        assertEquals(3, jdbcTemplate.queryForObject("SELECT count(*) FROM nw.payment WHERE invoice_id = ? and payment_id = ? ",
                new Object[]{invoiceId, paymentId}, Integer.class).intValue());

        Payment payment = paymentDao.get(invoiceId, paymentId);
        assertEquals(PaymentStatus.captured, payment.getStatus());
        assertEquals("high", payment.getRiskScore().name());
        assertEquals(1, jdbcTemplate.queryForObject("SELECT count(*) FROM nw.cash_flow WHERE obj_id = ? ",
                new Object[]{payment.getId()}, Integer.class).intValue());

        //--- second changes - only update

        List<MachineEvent> machineEventsSecond = List.of(
                new MachineEvent().setSourceId(invoiceId)
                        .setEventId(2)
                        .setCreatedAt(TypeUtil.temporalToString(LocalDateTime.now()))
                        .setData(Value.bin(serializer.serialize(
                                EventPayload.invoice_changes(
                                        List.of(InvoiceChange.invoice_payment_change(new InvoicePaymentChange()
                                                        .setId(paymentId)
                                                        .setPayload(InvoicePaymentChangePayload.invoice_payment_risk_score_changed(
                                                                new InvoicePaymentRiskScoreChanged(RiskScore.low)))),
                                                InvoiceChange.invoice_payment_change(new InvoicePaymentChange()
                                                        .setId(paymentId)
                                                        .setPayload(InvoicePaymentChangePayload.invoice_payment_rec_token_acquired(
                                                                new InvoicePaymentRecTokenAcquired("keks")
                                                        )))
                                        )))))
        );

        invoicingService.handleEvents(machineEventsSecond);

        assertEquals(3, jdbcTemplate.queryForObject("SELECT count(*) FROM nw.payment WHERE invoice_id = ? and payment_id = ? ",
                new Object[]{invoiceId, paymentId}, Integer.class).intValue());

        Payment paymentSecond = paymentDao.get(invoiceId, paymentId);
        assertEquals("low", paymentSecond.getRiskScore().name());
        assertEquals("keks", paymentSecond.getRecurrentIntentionToken());
        assertEquals(paymentSecond.getId(), payment.getId());

        //--- third changes - insert

        List<MachineEvent> machineEventsThird = List.of(
                new MachineEvent().setSourceId(invoiceId)
                        .setEventId(3)
                        .setCreatedAt(TypeUtil.temporalToString(LocalDateTime.now()))
                        .setData(Value.bin(serializer.serialize(
                                EventPayload.invoice_changes(
                                        List.of(InvoiceChange.invoice_payment_change(new InvoicePaymentChange()
                                                .setId(paymentId)
                                                .setPayload(InvoicePaymentChangePayload.invoice_payment_status_changed(
                                                        new InvoicePaymentStatusChanged().setStatus(InvoicePaymentStatus.failed(
                                                                new InvoicePaymentFailed(OperationFailure.operation_timeout(new OperationTimeout()))))
                                                ))))))))
        );

        invoicingService.handleEvents(machineEventsThird);

        assertEquals(4, jdbcTemplate.queryForObject("SELECT count(*) FROM nw.payment WHERE invoice_id = ? and payment_id = ? ",
                new Object[]{invoiceId, paymentId}, Integer.class).intValue());

        Payment paymentThird = paymentDao.get(invoiceId, paymentId);
        assertEquals(PaymentStatus.failed, paymentThird.getStatus());
        assertEquals(3, paymentThird.getSequenceId().longValue());
        assertNotEquals(paymentSecond.getId(), paymentThird.getId());

        assertEquals(1, jdbcTemplate.queryForObject("SELECT count(*) FROM nw.cash_flow WHERE obj_id = ? ",
                new Object[]{paymentThird.getId()}, Integer.class).intValue());

        //--- duplication check

        invoicingService.handleEvents(machineEventsFirst);
        assertEquals(4, jdbcTemplate.queryForObject("SELECT count(*) FROM nw.payment WHERE invoice_id = ? and payment_id = ? ",
                new Object[]{invoiceId, paymentId}, Integer.class).intValue());
    }
}
