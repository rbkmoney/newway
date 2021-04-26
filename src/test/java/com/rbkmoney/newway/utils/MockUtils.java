package com.rbkmoney.newway.utils;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.geck.common.util.TypeUtil;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class MockUtils {

    public static Invoice buildInvoice(String invoiceId) {
        return new Invoice()
                .setId(invoiceId)
                .setOwnerId("party_1")
                .setShopId("shop_id")
                .setCreatedAt(TypeUtil.temporalToString(LocalDateTime.now()))
                .setStatus(InvoiceStatus.unpaid(new InvoiceUnpaid()))
                .setDetails(new InvoiceDetails()
                        .setProduct("prod")
                        .setCart(new InvoiceCart(
                                List.of(new InvoiceLine()
                                        .setQuantity(1)
                                        .setProduct("product")
                                        .setPrice(new Cash(12, new CurrencyRef("RUB")))
                                        .setMetadata(new HashMap<>())))))
                .setDue(TypeUtil.temporalToString(LocalDateTime.now()))
                .setCost(new Cash().setAmount(1).setCurrency(new CurrencyRef("RUB")))
                .setContext(new Content("type", ByteBuffer.wrap(new byte[] {})));
    }

    public static InvoicePayment buildPayment(String paymentId) {
        return new InvoicePayment()
                .setId(paymentId)
                .setCreatedAt(TypeUtil.temporalToString(LocalDateTime.now()))
                .setStatus(InvoicePaymentStatus.pending(new InvoicePaymentPending()))
                .setCost(new Cash(11, new CurrencyRef("RUB")))
                .setDomainRevision(1)
                .setFlow(InvoicePaymentFlow.instant(new InvoicePaymentFlowInstant()))
                .setPayer(Payer.recurrent(
                        new RecurrentPayer()
                                .setPaymentTool(PaymentTool.payment_terminal(
                                        new PaymentTerminal()
                                                .setTerminalTypeDeprecated(LegacyTerminalPaymentProvider.alipay)
                                ))
                                .setRecurrentParent(new RecurrentParentPayment("1", "2"))
                                .setContactInfo(new ContactInfo())));
    }
}
