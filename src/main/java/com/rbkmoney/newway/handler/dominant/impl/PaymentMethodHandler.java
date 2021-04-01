package com.rbkmoney.newway.handler.dominant.impl;

import com.rbkmoney.damsel.domain.PaymentMethodObject;
import com.rbkmoney.damsel.domain.TokenizedBankCard;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.dao.dominant.impl.PaymentMethodDaoImpl;
import com.rbkmoney.newway.domain.enums.PaymentMethodType;
import com.rbkmoney.newway.domain.tables.pojos.PaymentMethod;
import com.rbkmoney.newway.handler.dominant.AbstractDominantHandler;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodHandler extends AbstractDominantHandler<PaymentMethodObject, PaymentMethod, String> {

    private static final String TOKENIZED_BANK_CARD_SEPARATOR = "_";
    private static final String SEPARATOR = ".";
    private static final String EMPTY_CVV = "empty_cvv_";
    private static final String DEPRECATED = "_deprecated";
    private final PaymentMethodDaoImpl paymentMethodDao;

    public PaymentMethodHandler(PaymentMethodDaoImpl paymentMethodDao) {
        this.paymentMethodDao = paymentMethodDao;
    }

    @Override
    protected DomainObjectDao<PaymentMethod, String> getDomainObjectDao() {
        return paymentMethodDao;
    }

    @Override
    protected PaymentMethodObject getTargetObject() {
        return getDomainObject().getPaymentMethod();
    }

    @Override
    protected String getTargetObjectRefId() {
        var paymentMethodObjectRefId = getTargetObject().getRef().getId();
        String paymentMethodRefId;
        if (paymentMethodObjectRefId.isSetBankCard()) {
            paymentMethodRefId = paymentMethodObjectRefId.getBankCard().getPaymentSystem().name();
        } else if (paymentMethodObjectRefId.isSetPaymentTerminal()) {
            paymentMethodRefId = paymentMethodObjectRefId.getPaymentTerminal().name();
        } else if (paymentMethodObjectRefId.isSetDigitalWallet()) {
            paymentMethodRefId = paymentMethodObjectRefId.getDigitalWallet().name();
        } else if (paymentMethodObjectRefId.isSetTokenizedBankCardDeprecated()) {
            paymentMethodRefId = getTokenizedBankCardId(paymentMethodObjectRefId.getTokenizedBankCardDeprecated());
        } else if (paymentMethodObjectRefId.isSetEmptyCvvBankCardDeprecated()) {
            paymentMethodRefId = EMPTY_CVV + paymentMethodObjectRefId.getEmptyCvvBankCardDeprecated().name();
        } else if (paymentMethodObjectRefId.isSetCryptoCurrency()) {
            paymentMethodRefId = paymentMethodObjectRefId.getCryptoCurrency().name();
        } else if (paymentMethodObjectRefId.isSetMobile()) {
            paymentMethodRefId = paymentMethodObjectRefId.getMobile().name();
        } else if (paymentMethodObjectRefId.isSetBankCardDeprecated()) {
            paymentMethodRefId = paymentMethodObjectRefId.getBankCardDeprecated().name();
        } else {
            throw new IllegalArgumentException("Unknown payment method: " + paymentMethodObjectRefId);
        }

        return getPaymentType(getTargetObject()) + SEPARATOR + paymentMethodRefId;
    }

    private String getTokenizedBankCardId(TokenizedBankCard card) {
        return card.getPaymentSystem().name() + TOKENIZED_BANK_CARD_SEPARATOR + card.getTokenProvider().name();
    }

    private String getPaymentType(PaymentMethodObject pmObj) {
        return pmObj.getRef().getId().getSetField().getFieldName().replaceAll(DEPRECATED, "");
    }

    @Override
    protected boolean acceptDomainObject() {
        return getDomainObject().isSetPaymentMethod();
    }

    @Override
    public PaymentMethod convertToDatabaseObject(PaymentMethodObject paymentMethodObject, Long versionId,
                                                 boolean current) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setVersionId(versionId);
        paymentMethod.setPaymentMethodRefId(getTargetObjectRefId());
        var data = paymentMethodObject.getData();
        paymentMethod.setName(data.getName());
        paymentMethod.setDescription(data.getDescription());
        paymentMethod.setType(
                Enum.valueOf(
                        PaymentMethodType.class,
                        getPaymentType(paymentMethodObject)
                )
        );
        paymentMethod.setCurrent(current);
        return paymentMethod;
    }
}
