package com.rbkmoney.newway.model;

public enum InvoicingType {
    INVOICE("invoice"),
    PAYMENT("payment");

    private String value;

    InvoicingType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static InvoicingType lookup(String v) {
        for (InvoicingType e : values()) {
            if (e.value().equals(v)) {
                return e;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
