package com.rbkmoney.newway.model;

import lombok.Data;

@Data
public abstract class Wrapper {
    private boolean shouldInsert;
    private InvoicingKey key;
}
