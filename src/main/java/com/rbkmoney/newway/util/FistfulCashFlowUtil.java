package com.rbkmoney.newway.util;

import com.rbkmoney.fistful.cashflow.*;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.newway.domain.enums.CashFlowAccount;
import com.rbkmoney.newway.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.newway.domain.tables.pojos.FistfulCashFlow;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FistfulCashFlowUtil {
    public static List<FistfulCashFlow> convertFistfulCashFlows(List<FinalCashFlowPosting> cashFlowPostings, Long objId,
                                                                FistfulCashFlowChangeType cashFlowChangeType) {
        return cashFlowPostings.stream().map(cf -> {
            FistfulCashFlow fcf = new FistfulCashFlow();
            fcf.setObjId(objId);
            fcf.setObjType(cashFlowChangeType);
            fcf.setSourceAccountType(
                    TBaseUtil.unionFieldToEnum(cf.getSource().getAccountType(), CashFlowAccount.class));
            fcf.setSourceAccountTypeValue(getCashFlowAccountTypeValue(cf.getSource()));
            fcf.setSourceAccountId(cf.getSource().getAccountId());
            fcf.setDestinationAccountType(
                    TBaseUtil.unionFieldToEnum(cf.getDestination().getAccountType(), CashFlowAccount.class));
            fcf.setDestinationAccountTypeValue(getCashFlowAccountTypeValue(cf.getDestination()));
            fcf.setDestinationAccountId(cf.getDestination().getAccountId());
            fcf.setAmount(cf.getVolume().getAmount());
            fcf.setCurrencyCode(cf.getVolume().getCurrency().getSymbolicCode());
            fcf.setDetails(cf.getDetails());
            return fcf;
        }).collect(Collectors.toList());
    }

    public static String getCashFlowAccountTypeValue(FinalCashFlowAccount cfa) {
        if (cfa.getAccountType().isSetMerchant()) {
            return cfa.getAccountType().getMerchant().name();
        } else if (cfa.getAccountType().isSetProvider()) {
            return cfa.getAccountType().getProvider().name();
        } else if (cfa.getAccountType().isSetSystem()) {
            return cfa.getAccountType().getSystem().name();
        } else if (cfa.getAccountType().isSetExternal()) {
            return cfa.getAccountType().getExternal().name();
        } else if (cfa.getAccountType().isSetWallet()) {
            return cfa.getAccountType().getWallet().name();
        } else {
            throw new IllegalArgumentException("Illegal fistful cash flow account type: " + cfa.getAccountType());
        }
    }

    public static long getFistfulFee(List<com.rbkmoney.fistful.cashflow.FinalCashFlowPosting> postings) {
        return getFistfulAmount(
                postings,
                posting -> posting.getSource().getAccountType().isSetWallet()
                        && posting.getDestination().getAccountType().isSetSystem()
        );
    }

    public static long getFistfulProviderFee(List<com.rbkmoney.fistful.cashflow.FinalCashFlowPosting> postings) {
        return getFistfulAmount(
                postings,
                posting -> posting.getSource().getAccountType().isSetSystem()
                        && posting.getDestination().getAccountType().isSetProvider()
        );
    }

    public static long getFistfulAmount(
            List<com.rbkmoney.fistful.cashflow.FinalCashFlowPosting> postings,
            Predicate<FinalCashFlowPosting> filter
    ) {
        return postings.stream()
                .filter(filter)
                .map(posting -> posting.getVolume().getAmount())
                .reduce(0L, Long::sum);
    }

    public static Long computeAmount(List<FinalCashFlowPosting> finalCashFlow) {
        long amountSource = computeAmount(finalCashFlow, FinalCashFlowPosting::getSource);
        long amountDest = computeAmount(finalCashFlow, FinalCashFlowPosting::getDestination);
        return amountDest - amountSource;
    }

    private static long computeAmount(List<FinalCashFlowPosting> finalCashFlow,
                                      Function<FinalCashFlowPosting, FinalCashFlowAccount> func) {
        return finalCashFlow.stream()
                .filter(f -> isMerchantSettlement(func.apply(f).getAccountType()))
                .mapToLong(cashFlow -> cashFlow.getVolume().getAmount())
                .sum();
    }

    private static boolean isMerchantSettlement(com.rbkmoney.fistful.cashflow.CashFlowAccount cashFlowAccount) {
        return cashFlowAccount.isSetMerchant()
                && cashFlowAccount.getMerchant() == MerchantCashFlowAccount.settlement;
    }
}
