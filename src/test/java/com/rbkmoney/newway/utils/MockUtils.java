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
                .setContext(new Content("type", ByteBuffer.wrap(new byte[]{})));
    }
}
