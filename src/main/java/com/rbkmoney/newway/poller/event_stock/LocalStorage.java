package com.rbkmoney.newway.poller.event_stock;

import com.rbkmoney.newway.model.InvoiceWrapper;
import com.rbkmoney.newway.model.InvoicingKey;
import com.rbkmoney.newway.model.InvoicingType;
import com.rbkmoney.newway.model.PaymentWrapper;

import java.util.HashMap;
import java.util.Map;

public class LocalStorage {
    private Map<InvoicingKey, Object> map = new HashMap<>();

    public Object get(InvoicingKey key) {
        return map.get(key);
    }

    public Object getCopy(InvoicingKey key) {
        Object source = map.get(key);
        if (source == null) {
            return null;
        }
        if (key.getType() == InvoicingType.INVOICE) {
            InvoiceWrapper sourceWrapper = (InvoiceWrapper) source;
            return sourceWrapper.copy();
        } else if (key.getType() == InvoicingType.PAYMENT) {
            PaymentWrapper sourceWrapper = (PaymentWrapper) source;
            return sourceWrapper.copy();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void put(InvoicingKey key, Object object) {
        map.put(key, object);
    }
}
