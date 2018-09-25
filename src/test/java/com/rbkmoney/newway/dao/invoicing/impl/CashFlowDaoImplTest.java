package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.Assert.*;

public class CashFlowDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private CashFlowDao cashFlowDao;

    @Autowired
    private PaymentDao paymentDao;

    @Test
    public void test() {
        Payment payment = random(Payment.class);
        payment.setCurrent(true);
        Long pmntId = paymentDao.save(payment);
        List<CashFlow> cashFlowList = randomListOf(100, CashFlow.class);
        cashFlowList.forEach(cf -> {
            cf.setObjId(pmntId);
            cf.setAmount((long) new Random().nextInt(100));
            cf.setObjType(PaymentChangeType.payment);
            cf.setAdjFlowType(null);
            cf.setSourceAccountTypeValue("settlement");
            if (cf.getDestinationAccountType() == com.rbkmoney.newway.domain.enums.CashFlowAccount.external) {
                cf.setDestinationAccountTypeValue("income");
            } else {
                cf.setDestinationAccountTypeValue("settlement");
            }
        });
        cashFlowDao.save(cashFlowList);
        List<CashFlow> byObjId = cashFlowDao.getByObjId(pmntId, PaymentChangeType.payment);
        assertEquals(new HashSet(byObjId), new HashSet(cashFlowList));
        paymentDao.updateCommissions(pmntId);
        Payment paymentWithCommissions = paymentDao.get(payment.getInvoiceId(), payment.getPaymentId());
        Map<FeeType, Long> fees = getFees(cashFlowList);
        assertEquals(paymentWithCommissions.getFee(), fees.getOrDefault(FeeType.FEE, 0L));
        assertEquals(paymentWithCommissions.getProviderFee(), fees.getOrDefault(FeeType.PROVIDER_FEE, 0L));
        assertEquals(paymentWithCommissions.getExternalFee(), fees.getOrDefault(FeeType.EXTERNAL_FEE, 0L));
        assertEquals(paymentWithCommissions.getGuaranteeDeposit(), fees.getOrDefault(FeeType.GUARANTEE_DEPOSIT, 0L));
    }

    public static Map<FeeType, Long> getFees(List<CashFlow> cashFlowList) {
        return cashFlowList.stream()
                .collect(
                        Collectors.groupingBy(
                                CashFlowDaoImplTest::getFeeType,
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
                && (destinationValue.equals(ExternalCashFlowAccount.income.name()) || destinationValue.equals(ExternalCashFlowAccount.outcome.name()))) {
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