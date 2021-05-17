package com.rbkmoney.newway.util;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.mamsel.PaymentSystemUtil;
import com.rbkmoney.mamsel.TokenProviderUtil;

import java.util.Optional;
import java.util.function.Supplier;

public class PaymentMethodUtils {

    private static final String TOKENIZED_BANK_CARD_SEPARATOR = "_";
    private static final String EMPTY_CVV = "empty_cvv_";

    public static Optional<String> getPaymentMethodRefIdByBankCard(
            Supplier<Optional<PaymentMethod>> paymentMethod) {
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
                        .flatMap(PaymentMethodUtils::getTokenizedBankCardId));
    }

    private static Optional<String> getTokenizedBankCardId(TokenizedBankCard tokenizedBankCard) {
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

    public static Optional<String> getPaymentMethodRefIdByPaymentTerminal(
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

    public static Optional<String> getPaymentMethodRefIdByDigitalWallet(
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

    public static Optional<String> getPaymentMethodRefIdByCryptoCurrency(
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

    public static Optional<String> getPaymentMethodRefIdByMobile(
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
}
