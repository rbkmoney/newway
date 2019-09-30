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
}
