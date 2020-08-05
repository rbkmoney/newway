package com.rbkmoney.newway.poller.dominant.impl;

import com.rbkmoney.damsel.domain.PaymentMethodObject;
import com.rbkmoney.damsel.domain.TokenizedBankCard;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.dao.dominant.impl.PaymentMethodDaoImpl;
import com.rbkmoney.newway.domain.enums.PaymentMethodType;
import com.rbkmoney.newway.domain.tables.pojos.PaymentMethod;
import com.rbkmoney.newway.poller.dominant.AbstractDominantHandler;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodHandler extends AbstractDominantHandler<PaymentMethodObject, PaymentMethod, String> {

    private final PaymentMethodDaoImpl paymentMethodDao;

    public PaymentMethodHandler(PaymentMethodDaoImpl paymentMethodDao) {
        this.paymentMethodDao = paymentMethodDao;
    }

    @Override
    protected DomainObjectDao<PaymentMethod, String> getDomainObjectDao() {
        return paymentMethodDao;
    }

    @Override
    protected PaymentMethodObject getObject() {
        return getDomainObject().getPaymentMethod();
    }

    @Override
    protected String getObjectRefId() {
        com.rbkmoney.damsel.domain.PaymentMethod paymentMethodObjectRefId = getObject().getRef().getId();
        String paymentMethodRefId;
        if (paymentMethodObjectRefId.isSetBankCard()) {
            paymentMethodRefId = paymentMethodObjectRefId.getBankCard().getPaymentSystem().name();
        } else if (paymentMethodObjectRefId.isSetPaymentTerminal()) {
            paymentMethodRefId = paymentMethodObjectRefId.getPaymentTerminal().name();
        } else if (paymentMethodObjectRefId.isSetDigitalWallet()) {
            paymentMethodRefId = paymentMethodObjectRefId.getDigitalWallet().name();
        } else if (paymentMethodObjectRefId.isSetTokenizedBankCardDeprecated()) {
            TokenizedBankCard tokenizedBankCard = paymentMethodObjectRefId.getTokenizedBankCardDeprecated();
            paymentMethodRefId = tokenizedBankCard.getPaymentSystem().name() + "_" + tokenizedBankCard.getTokenProvider().name();
        } else if (paymentMethodObjectRefId.isSetEmptyCvvBankCardDeprecated()) {
            paymentMethodRefId = "empty_cvv_" + paymentMethodObjectRefId.getEmptyCvvBankCardDeprecated().name();
        } else if (paymentMethodObjectRefId.isSetCryptoCurrency()) {
            paymentMethodRefId = paymentMethodObjectRefId.getCryptoCurrency().name();
        } else if (paymentMethodObjectRefId.isSetMobile()) {
            paymentMethodRefId = paymentMethodObjectRefId.getMobile().name();
        } else if (paymentMethodObjectRefId.isSetBankCardDeprecated()) {
            paymentMethodRefId = paymentMethodObjectRefId.getBankCardDeprecated().name();
        } else {
            throw new IllegalArgumentException("Unknown payment method: " + paymentMethodObjectRefId);
        }
        return paymentMethodRefId;
    }

    @Override
    protected boolean acceptDomainObject() {
        return getDomainObject().isSetPaymentMethod();
    }

    @Override
    public PaymentMethod convertToDatabaseObject(PaymentMethodObject paymentMethodObject, Long versionId, boolean current) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setVersionId(versionId);
        paymentMethod.setPaymentMethodRefId(getObjectRefId());
        com.rbkmoney.damsel.domain.PaymentMethodDefinition data = paymentMethodObject.getData();
        paymentMethod.setName(data.getName());
        paymentMethod.setDescription(data.getDescription());
        paymentMethod.setType(TBaseUtil.unionFieldToEnum(paymentMethodObject.getRef().getId(), PaymentMethodType.class));
        paymentMethod.setCurrent(current);
        return paymentMethod;
    }
}
