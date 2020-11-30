package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.AbstractAppDaoTests;
import com.rbkmoney.newway.dao.invoicing.impl.PaymentDaoImpl;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.model.InvoicingKey;
import com.rbkmoney.newway.model.PaymentWrapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PaymentBatchServiceTest extends AbstractAppDaoTests {

    @Autowired
    private PaymentBatchService paymentBatchService;

    @Autowired
    private PaymentDaoImpl paymentDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void processTest() {
        List<PaymentWrapper> paymentWrappers = IntStream.range(1, 5)
                .mapToObj(x -> new PaymentWrapper(
                        random(Payment.class, "id"),
                        randomListOf(3, CashFlow.class, "id", "objId"),
                        true,
                        null))
                .collect(Collectors.toList());

        String invoiceIdFirst = "invoiceIdFirst";
        String invoiceIdSecond = "invoiceIdSecond";
        paymentWrappers.get(0).getPayment().setInvoiceId(invoiceIdFirst);
        paymentWrappers.get(0).getPayment().setPaymentId("1");
        paymentWrappers.get(1).getPayment().setInvoiceId(invoiceIdFirst);
        paymentWrappers.get(1).getPayment().setPaymentId("1");
        paymentWrappers.get(2).getPayment().setInvoiceId(invoiceIdFirst);
        paymentWrappers.get(2).getPayment().setPaymentId("2");
        paymentWrappers.get(3).getPayment().setInvoiceId(invoiceIdSecond);
        paymentWrappers.get(3).getPayment().setPaymentId("1");
        paymentWrappers.forEach(iw -> {
            iw.setKey(InvoicingKey.buildKey(iw));
            iw.getPayment().setCurrent(false);
            iw.getCashFlows().forEach(c -> c.setObjType(PaymentChangeType.payment));
        });
        paymentBatchService.process(paymentWrappers);

        Payment paymentFirstGet = paymentDao.get(invoiceIdFirst, "1");
        assertNotEquals(paymentWrappers.get(0).getPayment().getPartyId(), paymentFirstGet.getPartyId());
        assertEquals(paymentWrappers.get(1).getPayment().getPartyId(), paymentFirstGet.getPartyId());

        Payment paymentSecondGet = paymentDao.get(invoiceIdFirst, "2");
        assertEquals(paymentWrappers.get(2).getPayment().getShopId(), paymentSecondGet.getShopId());

        //Duplication check
        paymentBatchService.process(paymentWrappers);
        Payment paymentFirstGet2 = paymentDao.get(invoiceIdFirst, "1");
        assertEquals(paymentWrappers.get(1).getPayment().getPartyId(), paymentFirstGet2.getPartyId());
        assertEquals(2, jdbcTemplate.queryForObject("SELECT count(*) FROM nw.payment WHERE invoice_id = ? and payment_id = ? ", new Object[]{invoiceIdFirst, "1"}, Integer.class).intValue());
        assertEquals(1, jdbcTemplate.queryForObject("SELECT count(*) FROM nw.payment WHERE invoice_id = ? and payment_id = ? ", new Object[]{invoiceIdFirst, "2"}, Integer.class).intValue());
        assertEquals(1, jdbcTemplate.queryForObject("SELECT count(*) FROM nw.payment WHERE invoice_id = ? and payment_id = ? ", new Object[]{invoiceIdSecond, "1"}, Integer.class).intValue());
        assertEquals(24, jdbcTemplate.queryForObject("SELECT count(*) FROM nw.cash_flow ", Integer.class).intValue());
    }
}
