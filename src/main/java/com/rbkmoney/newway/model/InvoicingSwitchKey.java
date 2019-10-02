package com.rbkmoney.newway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class InvoicingSwitchKey {
    private String invoiceId;
    private String paymentId;
    private Long id;
}
