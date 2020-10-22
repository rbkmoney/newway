package com.rbkmoney.newway.poller.event_stock.impl.invoicing.payment;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.model.PaymentWrapper;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingMapper;

public abstract class AbstractInvoicingPaymentMapper extends AbstractInvoicingMapper<PaymentWrapper> {

    protected void setDefaultProperties(Payment payment, Long sequenceId, Integer changeId, String eventCreatedAt){
        payment.setWtime(null);
        payment.setCurrent(false);
        payment.setChangeId(changeId);
        payment.setSequenceId(sequenceId);
        payment.setEventCreatedAt(TypeUtil.stringToLocalDateTime(eventCreatedAt));
        payment.setSessionPayloadTransactionBoundTrxExtraJson(null);
        payment.setTrxAdditionalInfoAcsUrl(null);
        payment.setTrxAdditionalInfoPareq(null);
        payment.setTrxAdditionalInfoMd(null);
        payment.setTrxAdditionalInfoTermUrl(null);
        payment.setTrxAdditionalInfoPares(null);
        payment.setTrxAdditionalInfoCavv(null);
        payment.setTrxAdditionalInfoXid(null);
        payment.setTrxAdditionalInfoCavvAlgorithm(null);
        payment.setTrxAdditionalInfoThreeDsVerification(null);
    }
}
