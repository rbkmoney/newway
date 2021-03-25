package com.rbkmoney.newway.dao;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.newway.domain.enums.CashFlowAccount;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DaoUtils {

    public static CashFlow createCashFlow(long objId, long amount, String currencyCode, long sourceAccountId,
                                          CashFlowAccount provider, String sourceAccountTypeValue,
                                          long destinationAccountId, CashFlowAccount destinationAccountType,
                                          String destinationAccountTypeValue, PaymentChangeType paymentChangeType) {
        CashFlow cashFlowPaymentAmount = new CashFlow();
        cashFlowPaymentAmount.setObjId(objId);
        cashFlowPaymentAmount.setAmount(amount);
        cashFlowPaymentAmount.setCurrencyCode(currencyCode);
        cashFlowPaymentAmount.setSourceAccountId(sourceAccountId);
        cashFlowPaymentAmount.setSourceAccountType(provider);
        cashFlowPaymentAmount.setSourceAccountTypeValue(sourceAccountTypeValue);
        cashFlowPaymentAmount.setDestinationAccountId(destinationAccountId);
        cashFlowPaymentAmount.setDestinationAccountType(destinationAccountType);
        cashFlowPaymentAmount.setDestinationAccountTypeValue(destinationAccountTypeValue);
        cashFlowPaymentAmount.setObjType(paymentChangeType);
        return cashFlowPaymentAmount;
    }

    public static Map<FeeType, Long> getFees(List<CashFlow> cashFlowList) {
        return cashFlowList.stream()
                .collect(
                        Collectors.groupingBy(
                                DaoUtils::getFeeType,
                                Collectors.summingLong(CashFlow::getAmount)
                        )
                );
    }

    public static FeeType getFeeType(CashFlow cashFlow) {
        com.rbkmoney.newway.domain.enums.CashFlowAccount source = cashFlow.getSourceAccountType();
        String sourceValue = cashFlow.getSourceAccountTypeValue();
        com.rbkmoney.newway.domain.enums.CashFlowAccount destination = cashFlow.getDestinationAccountType();
        String destinationValue = cashFlow.getDestinationAccountTypeValue();

        if (source == com.rbkmoney.newway.domain.enums.CashFlowAccount.merchant
                && sourceValue.equals(MerchantCashFlowAccount.settlement.name())
                && destination == com.rbkmoney.newway.domain.enums.CashFlowAccount.system
                && destinationValue.equals(SystemCashFlowAccount.settlement.name())) {
            return FeeType.FEE;
        }

        if (source == com.rbkmoney.newway.domain.enums.CashFlowAccount.system
                && sourceValue.equals(SystemCashFlowAccount.settlement.name())
                && destination == com.rbkmoney.newway.domain.enums.CashFlowAccount.external
                && (destinationValue.equals(ExternalCashFlowAccount.income.name()) ||
                destinationValue.equals(ExternalCashFlowAccount.outcome.name()))) {
            return FeeType.EXTERNAL_FEE;
        }

        if (source == com.rbkmoney.newway.domain.enums.CashFlowAccount.system
                && sourceValue.equals(SystemCashFlowAccount.settlement.name())
                && destination == com.rbkmoney.newway.domain.enums.CashFlowAccount.provider
                && destinationValue.equals(ProviderCashFlowAccount.settlement.name())
        ) {
            return FeeType.PROVIDER_FEE;
        }

        if (source == com.rbkmoney.newway.domain.enums.CashFlowAccount.merchant
                && sourceValue.equals(MerchantCashFlowAccount.settlement.name())
                && destination == com.rbkmoney.newway.domain.enums.CashFlowAccount.merchant
                && destinationValue.equals(MerchantCashFlowAccount.guarantee.name())
        ) {
            return FeeType.GUARANTEE_DEPOSIT;
        }

        return FeeType.UNKNOWN;
    }

    public enum FeeType {
        UNKNOWN,
        FEE,
        PROVIDER_FEE,
        EXTERNAL_FEE,
        GUARANTEE_DEPOSIT
    }
}
