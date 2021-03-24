package com.rbkmoney.newway.service;

import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.model.InvoicingKey;
import com.rbkmoney.newway.model.PaymentWrapper;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class PaymentSquashServiceTest {

    @Test
    public void squashSimpleTest() {
        PaymentSquashService service = new PaymentSquashService();
        PaymentWrapper paymentWrapper = buildPaymentWrapper("inv_id", "1", 666L, true);
        List<PaymentWrapper> squashedWrappers = service.squash(List.of(paymentWrapper), List.of(1L));
        assertEquals(1, squashedWrappers.size());
        assertTrue(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getPayment().getId().longValue(), 1);
    }

    @Test
    public void squashSimple0Test() {
        PaymentSquashService service = new PaymentSquashService();
        PaymentWrapper paymentWrapper = buildPaymentWrapper("inv_id", "1", 666L, false);
        List<PaymentWrapper> squashedWrappers = service.squash(List.of(paymentWrapper), List.of(1L));
        assertEquals(1, squashedWrappers.size());
        assertFalse(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getPayment().getId().longValue(), 666);
    }

    @Test
    public void squashSimple1Test() {
        PaymentSquashService service = new PaymentSquashService();
        PaymentWrapper pw1 = buildPaymentWrapper("inv_id", "1", 666L, true);
        PaymentWrapper pw2 = buildPaymentWrapper("inv_id", "1", 667L, 2L, false);
        List<PaymentWrapper> squashedWrappers = service.squash(List.of(pw1, pw2), List.of(1L, 2L));
        assertEquals(1, squashedWrappers.size());
        assertTrue(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getPayment().getId().longValue(), 1);
        assertEquals(squashedWrappers.get(0).getPayment().getPartyRevision(), pw2.getPayment().getPartyRevision());
    }

    @Test
    public void squashSimple11Test() {
        PaymentSquashService service = new PaymentSquashService();
        PaymentWrapper pw1 = buildPaymentWrapper("inv_id", "1", 666L, false);
        PaymentWrapper pw2 = buildPaymentWrapper("inv_id", "1", 667L, 2L, false);
        List<PaymentWrapper> squashedWrappers = service.squash(List.of(pw1, pw2), List.of(1L, 2L));
        assertEquals(1, squashedWrappers.size());
        assertFalse(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getPayment().getId().longValue(), 666L);
        assertEquals(squashedWrappers.get(0).getPayment().getPartyRevision(), pw2.getPayment().getPartyRevision());
    }

    @Test
    public void squashSimple12Test() {
        PaymentSquashService service = new PaymentSquashService();
        PaymentWrapper pw1 = buildPaymentWrapper("inv_id", "1", 666L, true);
        PaymentWrapper pw2 = buildPaymentWrapper("inv_id", "1", 667L, 2L, true);
        List<PaymentWrapper> squashedWrappers = service.squash(List.of(pw1, pw2), List.of(1L, 2L));
        assertEquals(2, squashedWrappers.size());
        assertTrue(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getPayment().getId().longValue(), 1);
        assertEquals(squashedWrappers.get(0).getPayment().getPartyRevision().longValue(), 0);
        assertTrue(squashedWrappers.get(1).isShouldInsert());
        assertEquals(squashedWrappers.get(1).getPayment().getId().longValue(), 2);
        assertEquals(squashedWrappers.get(1).getPayment().getPartyRevision(), pw2.getPayment().getPartyRevision());
    }

    @Test
    public void squashSimple2Test() {
        PaymentSquashService service = new PaymentSquashService();
        PaymentWrapper pw1 = buildPaymentWrapper("inv_id", "1", 666L, false);
        PaymentWrapper pw2 = buildPaymentWrapper("inv_id", "1", 667L, 3L, true);
        List<PaymentWrapper> squashedWrappers = service.squash(List.of(pw1, pw2), List.of(1L, 2L));
        assertEquals(2, squashedWrappers.size());
        assertFalse(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getPayment().getId().longValue(), 666);
        assertEquals(squashedWrappers.get(0).getPayment().getPartyRevision().longValue(), 0);
        assertTrue(squashedWrappers.get(1).isShouldInsert());
        assertEquals(squashedWrappers.get(1).getPayment().getId().longValue(), 1);
        assertEquals(squashedWrappers.get(1).getPayment().getPartyRevision().longValue(), 3L);
    }

    @Test
    public void squashSimple3Test() {
        PaymentSquashService service = new PaymentSquashService();
        PaymentWrapper pw1 = buildPaymentWrapper("inv_id", "1", 666L, true);
        PaymentWrapper pw2 = buildPaymentWrapper("inv_id", "2", 667L, false);
        List<PaymentWrapper> squashedWrappers = service.squash(List.of(pw1, pw2), List.of(1L, 2L));
        assertEquals(2, squashedWrappers.size());
        assertTrue(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getPayment().getId().longValue(), 1);
        assertFalse(squashedWrappers.get(1).isShouldInsert());
        assertEquals(squashedWrappers.get(1).getPayment().getId().longValue(), 667);
    }

    @Test
    public void squashSimple4Test() {
        PaymentSquashService service = new PaymentSquashService();
        PaymentWrapper pw1 = buildPaymentWrapper("inv_id", "1", 666L, true);
        PaymentWrapper pw2 = buildPaymentWrapper("inv_id", "1", 667L, false);
        PaymentWrapper pw3 = buildPaymentWrapper("inv_id", "1", 668L, false);
        PaymentWrapper pw4 = buildPaymentWrapper("inv_id", "1", 669L, true);
        PaymentWrapper pw5 = buildPaymentWrapper("inv_id", "1", 670L, false);
        List<PaymentWrapper> squashedWrappers = service.squash(List.of(pw1, pw2, pw3, pw4, pw5), List.of(1L, 2L, 3L, 4L, 5L));
        assertEquals(2, squashedWrappers.size());
        assertTrue(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getPayment().getId().longValue(), 1);
        assertTrue(squashedWrappers.get(1).isShouldInsert());
        assertEquals(squashedWrappers.get(1).getPayment().getId().longValue(), 2);
    }

    @Test
    public void squashSimple41Test() {
        PaymentSquashService service = new PaymentSquashService();
        PaymentWrapper pw1 = buildPaymentWrapper("inv_id", "1", 666L, true);
        PaymentWrapper pw2 = buildPaymentWrapper("inv_id", "1", 667L, false);
        PaymentWrapper pw3 = buildPaymentWrapper("inv_id", "2", 668L, false);
        PaymentWrapper pw4 = buildPaymentWrapper("inv_id", "2", 669L, true);
        PaymentWrapper pw5 = buildPaymentWrapper("inv_id", "2", 670L, 2L, false);
        List<PaymentWrapper> squashedWrappers = service.squash(List.of(pw1, pw2, pw3, pw4, pw5), List.of(1L, 2L, 3L, 4L, 5L));
        assertEquals(3, squashedWrappers.size());
        assertTrue(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getPayment().getId().longValue(), 1);
        assertFalse(squashedWrappers.get(1).isShouldInsert());
        assertEquals(squashedWrappers.get(1).getPayment().getId().longValue(), 668);
        assertTrue(squashedWrappers.get(2).isShouldInsert());
        assertEquals(squashedWrappers.get(2).getPayment().getId().longValue(), 2);
        assertEquals(squashedWrappers.get(2).getPayment().getPaymentId(), "2");
        assertEquals(squashedWrappers.get(2).getPayment().getPartyRevision(), pw5.getPayment().getPartyRevision());

    }


    @Test
    public void squashSimple42Test() {
        PaymentSquashService service = new PaymentSquashService();
        PaymentWrapper pw1 = buildPaymentWrapper("inv_id", "1", 666L, true);
        PaymentWrapper pw2 = buildPaymentWrapper("inv_id", "1", 667L, false);
        PaymentWrapper pw3 = buildPaymentWrapper("inv_id", "2", 668L, true);
        PaymentWrapper pw4 = buildPaymentWrapper("inv_id", "2", 669L, false);
        PaymentWrapper pw5 = buildPaymentWrapper("inv_id", "1", 670L, false);
        List<PaymentWrapper> squashedWrappers = service.squash(List.of(pw1, pw2, pw3, pw4, pw5), List.of(1L, 2L, 3L, 4L, 5L));
        assertEquals(2, squashedWrappers.size());
        assertTrue(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getPayment().getId().longValue(), 1);
        assertTrue(squashedWrappers.get(1).isShouldInsert());
        assertEquals(squashedWrappers.get(1).getPayment().getId().longValue(), 2);
    }

    private PaymentWrapper buildPaymentWrapper(String invoiceId, String paymentId, Long id, Long partyRevision, boolean isShouldInsert) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setInvoiceId(invoiceId);
        payment.setPaymentId(paymentId);
        payment.setPartyRevision(partyRevision);
        PaymentWrapper paymentWrapper = new PaymentWrapper();
        paymentWrapper.setPayment(payment);
        paymentWrapper.setCashFlows(List.of(new CashFlow()));
        paymentWrapper.setKey(InvoicingKey.buildKey(invoiceId, paymentId));
        paymentWrapper.setShouldInsert(isShouldInsert);
        return paymentWrapper;
    }

    private PaymentWrapper buildPaymentWrapper(String invoiceId, String paymentId, Long id, boolean isShouldInsert) {
        return buildPaymentWrapper(invoiceId, paymentId, id, 0L, isShouldInsert);
    }
}
