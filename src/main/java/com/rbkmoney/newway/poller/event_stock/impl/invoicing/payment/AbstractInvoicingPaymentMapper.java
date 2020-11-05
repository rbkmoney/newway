package com.rbkmoney.newway.poller.event_stock.impl.invoicing.payment;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.model.PaymentWrapper;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingMapper;

public abstract class AbstractInvoicingPaymentMapper extends AbstractInvoicingMapper<PaymentWrapper> {

    protected void setInsertProperties(Payment payment, Long sequenceId, Integer changeId, String eventCreatedAt){
        payment.setWtime(null);
        payment.setId(null);
        payment.setChangeId(changeId);
        payment.setSequenceId(sequenceId);
        payment.setEventCreatedAt(TypeUtil.stringToLocalDateTime(eventCreatedAt));
    }

    protected void setUpdateProperties(Payment payment, String eventCreatedAt){
        payment.setWtime(null);
        payment.setEventCreatedAt(TypeUtil.stringToLocalDateTime(eventCreatedAt));
    }
}
