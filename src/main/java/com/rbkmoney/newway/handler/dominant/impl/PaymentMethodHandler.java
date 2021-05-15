package com.rbkmoney.newway.handler.dominant.impl;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.mamsel.PaymentSystemUtil;
import com.rbkmoney.mamsel.TokenProviderUtil;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.dao.dominant.impl.PaymentMethodDaoImpl;
import com.rbkmoney.newway.domain.enums.PaymentMethodType;
import com.rbkmoney.newway.domain.tables.pojos.PaymentMethod;
import com.rbkmoney.newway.handler.dominant.AbstractDominantHandler;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.function.Supplier;

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
        var paymentMethod = wrap(getTargetObject().getRef().getId());

        Optional<String> paymentMethodRefId = getPaymentMethodRefIdByBankCard(paymentMethod)
                .or(() -> getPaymentMethodRefIdByPaymentTerminal(paymentMethod))
                .or(() -> getPaymentMethodRefIdByDigitalWallet(paymentMethod))
                .or(() -> getPaymentMethodRefIdByCryptoCurrency(paymentMethod))
                .or(() -> getPaymentMethodRefIdByMobile(paymentMethod));

        if (paymentMethodRefId.isEmpty()) {
            throw new IllegalArgumentException("Unknown payment method: " + paymentMethod);
        }

        return getPaymentType(getTargetObject()) + SEPARATOR + paymentMethodRefId.get();
    }

    private Supplier<Optional<com.rbkmoney.damsel.domain.PaymentMethod>> wrap(
            @NotNull com.rbkmoney.damsel.domain.PaymentMethod paymentMethod) {
        return () -> Optional.of(paymentMethod);
    }

    public Optional<String> getPaymentMethodRefIdByBankCard(
            Supplier<Optional<com.rbkmoney.damsel.domain.PaymentMethod>> paymentMethod) {
        return paymentMethod.get()
                .filter(com.rbkmoney.damsel.domain.PaymentMethod::isSetBankCard)
                .map(com.rbkmoney.damsel.domain.PaymentMethod::getBankCard)
                .flatMap(bankCard -> PaymentSystemUtil.getPaymentSystemNameIfPresent(
                        bankCard.getPaymentSystem(),
                        bankCard.getPaymentSystemDeprecated()))
                .or(() -> paymentMethod.get()
                        .filter(com.rbkmoney.damsel.domain.PaymentMethod::isSetBankCardDeprecated)
                        .map(com.rbkmoney.damsel.domain.PaymentMethod::getBankCardDeprecated)
                        .map(Enum::name))
                .or(() -> paymentMethod.get()
                        .filter(com.rbkmoney.damsel.domain.PaymentMethod::isSetEmptyCvvBankCardDeprecated)
                        .map(com.rbkmoney.damsel.domain.PaymentMethod::getEmptyCvvBankCardDeprecated)
                        .map(legacyBankCardPaymentSystem -> EMPTY_CVV + legacyBankCardPaymentSystem.name()))
                .or(() -> paymentMethod.get()
                        .filter(com.rbkmoney.damsel.domain.PaymentMethod::isSetTokenizedBankCardDeprecated)
                        .map(com.rbkmoney.damsel.domain.PaymentMethod::getTokenizedBankCardDeprecated)
                        .flatMap(this::getTokenizedBankCardId));
    }

    private Optional<String> getTokenizedBankCardId(TokenizedBankCard tokenizedBankCard) {
        Optional<String> paymentSystemNameIfPresent = PaymentSystemUtil.getPaymentSystemNameIfPresent(
                tokenizedBankCard.getPaymentSystem(),
                tokenizedBankCard.getPaymentSystemDeprecated());
        Optional<String> tokenProviderNameIfPresent = TokenProviderUtil.getTokenProviderNameIfPresent(
                tokenizedBankCard.getPaymentToken(),
                tokenizedBankCard.getTokenProviderDeprecated());

        return paymentSystemNameIfPresent
                .flatMap(paymentSystemName -> tokenProviderNameIfPresent
                        .map(tokenProviderName -> paymentSystemName +
                                TOKENIZED_BANK_CARD_SEPARATOR +
                                tokenProviderName));
    }

    public Optional<String> getPaymentMethodRefIdByPaymentTerminal(
            Supplier<Optional<com.rbkmoney.damsel.domain.PaymentMethod>> paymentMethod) {
        return paymentMethod.get()
                .filter(com.rbkmoney.damsel.domain.PaymentMethod::isSetPaymentTerminal)
                .map(com.rbkmoney.damsel.domain.PaymentMethod::getPaymentTerminal)
                .map(PaymentServiceRef::getId)
                .or(() -> paymentMethod.get()
                        .filter(com.rbkmoney.damsel.domain.PaymentMethod::isSetPaymentTerminalDeprecated)
                        .map(com.rbkmoney.damsel.domain.PaymentMethod::getPaymentTerminalDeprecated)
                        .map(Enum::name));
    }

    public Optional<String> getPaymentMethodRefIdByDigitalWallet(
            Supplier<Optional<com.rbkmoney.damsel.domain.PaymentMethod>> paymentMethod) {
        return paymentMethod.get()
                .filter(com.rbkmoney.damsel.domain.PaymentMethod::isSetDigitalWallet)
                .map(com.rbkmoney.damsel.domain.PaymentMethod::getDigitalWallet)
                .map(PaymentServiceRef::getId)
                .or(() -> paymentMethod.get()
                        .filter(com.rbkmoney.damsel.domain.PaymentMethod::isSetDigitalWalletDeprecated)
                        .map(com.rbkmoney.damsel.domain.PaymentMethod::getDigitalWalletDeprecated)
                        .map(Enum::name));
    }

    public Optional<String> getPaymentMethodRefIdByCryptoCurrency(
            Supplier<Optional<com.rbkmoney.damsel.domain.PaymentMethod>> paymentMethod) {
        return paymentMethod.get()
                .filter(com.rbkmoney.damsel.domain.PaymentMethod::isSetCryptoCurrency)
                .map(com.rbkmoney.damsel.domain.PaymentMethod::getCryptoCurrency)
                .map(CryptoCurrencyRef::getId)
                .or(() -> paymentMethod.get()
                        .filter(com.rbkmoney.damsel.domain.PaymentMethod::isSetCryptoCurrencyDeprecated)
                        .map(com.rbkmoney.damsel.domain.PaymentMethod::getCryptoCurrencyDeprecated)
                        .map(Enum::name));
    }

    public Optional<String> getPaymentMethodRefIdByMobile(
            Supplier<Optional<com.rbkmoney.damsel.domain.PaymentMethod>> paymentMethod) {
        return paymentMethod.get()
                .filter(com.rbkmoney.damsel.domain.PaymentMethod::isSetMobile)
                .map(com.rbkmoney.damsel.domain.PaymentMethod::getMobile)
                .map(MobileOperatorRef::getId)
                .or(() -> paymentMethod.get()
                        .filter(com.rbkmoney.damsel.domain.PaymentMethod::isSetMobileDeprecated)
                        .map(com.rbkmoney.damsel.domain.PaymentMethod::getMobileDeprecated)
                        .map(Enum::name));
    }

    private String getPaymentType(PaymentMethodObject pmObj) {
        return pmObj.getRef().getId().getSetField().getFieldName().replaceAll(DEPRECATED, "");
    }

    @Override
    protected boolean acceptDomainObject() {
        return getDomainObject().isSetPaymentMethod();
    }

    @Override
    public PaymentMethod convertToDatabaseObject(
            PaymentMethodObject paymentMethodObject,
            Long versionId,
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
