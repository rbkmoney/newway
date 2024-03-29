package com.rbkmoney.newway.mapper.payment;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.mapper.AbstractInvoicingMapper;
import com.rbkmoney.newway.model.PaymentWrapper;

public abstract class AbstractInvoicingPaymentMapper extends AbstractInvoicingMapper<PaymentWrapper> {

    protected void setInsertProperties(Payment payment, Long sequenceId, Integer changeId, String eventCreatedAt) {
        payment.setId(null);
        payment.setWtime(null);
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

    protected void setUpdateProperties(Payment payment, String eventCreatedAt) {
        payment.setWtime(null);
        payment.setEventCreatedAt(TypeUtil.stringToLocalDateTime(eventCreatedAt));
    }
}
