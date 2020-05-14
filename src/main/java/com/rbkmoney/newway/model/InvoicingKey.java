package com.rbkmoney.newway.model;

import lombok.*;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@ToString
public class InvoicingKey {
    private String invoiceId;
    private String paymentId;
    private InvoicingType type;

    public static InvoicingKey buildKey(InvoiceWrapper iw) {
        return InvoicingKey.builder()
                .invoiceId(iw.getInvoice().getInvoiceId())
                .type(InvoicingType.INVOICE)
                .build();
    }

    public static InvoicingKey buildKey(PaymentWrapper pw) {
        return InvoicingKey.builder()
                .invoiceId(pw.getPayment().getInvoiceId())
                .paymentId(pw.getPayment().getPaymentId())
                .type(InvoicingType.PAYMENT)
                .build();
    }

    public static InvoicingKey buildKey(String invoiceId) {
        return InvoicingKey.builder()
                .invoiceId(invoiceId)
                .type(InvoicingType.INVOICE)
                .build();
    }

    public static InvoicingKey buildKey(String invoiceId, String paymentId) {
        return InvoicingKey.builder()
                .invoiceId(invoiceId)
                .paymentId(paymentId)
                .type(InvoicingType.PAYMENT)
                .build();
    }
}
