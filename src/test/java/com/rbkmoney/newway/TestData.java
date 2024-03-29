package com.rbkmoney.newway;

import com.rbkmoney.damsel.domain.InvoicePaymentChargeback;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import io.github.benas.randombeans.api.EnhancedRandom;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestData {

    public static InvoiceChange buildInvoiceChangeChargebackCreated() {
        InvoicePaymentChargeback invoicePaymentChargeback =
                EnhancedRandom.random(InvoicePaymentChargeback.class, "context", "status", "reason", "stage");
        invoicePaymentChargeback.setCreatedAt(TypeUtil.temporalToString(Instant.now()));
        InvoicePaymentChargebackStatus invoicePaymentChargebackStatus = buildChargebackStatus();
        invoicePaymentChargeback.setStatus(invoicePaymentChargebackStatus);

        InvoicePaymentChargebackReason invoicePaymentChargebackReason = buildChargebackReason();
        invoicePaymentChargeback.setReason(invoicePaymentChargebackReason);

        InvoicePaymentChargebackStage invoicePaymentChargebackStage = buildChargebackStage();
        invoicePaymentChargeback.setStage(invoicePaymentChargebackStage);

        InvoicePaymentChargebackChange invoicePaymentChargebackChange = new InvoicePaymentChargebackChange();
        invoicePaymentChargebackChange.setId("testChargebackId");
        InvoicePaymentChargebackChangePayload invoicePaymentChargebackChangePayload =
                new InvoicePaymentChargebackChangePayload();
        InvoicePaymentChargebackCreated invoicePaymentChargebackCreated = new InvoicePaymentChargebackCreated();
        invoicePaymentChargebackCreated.setChargeback(invoicePaymentChargeback);

        invoicePaymentChargebackChangePayload.setInvoicePaymentChargebackCreated(invoicePaymentChargebackCreated);
        invoicePaymentChargebackChange.setPayload(invoicePaymentChargebackChangePayload);

        InvoicePaymentChangePayload invoicePaymentChangePayload = new InvoicePaymentChangePayload();
        invoicePaymentChangePayload.setInvoicePaymentChargebackChange(invoicePaymentChargebackChange);

        InvoicePaymentChange invoicePaymentChange = new InvoicePaymentChange();
        invoicePaymentChange.setId("testPaymentId");
        invoicePaymentChange.setPayload(invoicePaymentChangePayload);

        InvoiceChange invoiceChange = new InvoiceChange();
        invoiceChange.setInvoicePaymentChange(invoicePaymentChange);

        return invoiceChange;
    }

    public static InvoiceChange buildInvoiceChangeChargebackStatusChanged() {
        InvoicePaymentChargebackStatusChanged invoicePaymentChargebackStatusChanged =
                new InvoicePaymentChargebackStatusChanged();
        invoicePaymentChargebackStatusChanged.setStatus(buildChargebackStatus());
        InvoicePaymentChargebackChangePayload invoicePaymentChargebackChangePayload =
                new InvoicePaymentChargebackChangePayload();
        invoicePaymentChargebackChangePayload
                .setInvoicePaymentChargebackStatusChanged(invoicePaymentChargebackStatusChanged);

        return buildInvoiceChangeChargeback(invoicePaymentChargebackChangePayload);
    }

    public static InvoiceChange buildInvoiceChangeChargebackLevyChanged() {
        InvoicePaymentChargebackLevyChanged invoicePaymentChargebackLevyChanged =
                new InvoicePaymentChargebackLevyChanged();
        Cash cash = new Cash().setAmount(1000L).setCurrency(new CurrencyRef().setSymbolicCode("456"));
        invoicePaymentChargebackLevyChanged.setLevy(cash);
        InvoicePaymentChargebackChangePayload invoicePaymentChargebackChangePayload =
                new InvoicePaymentChargebackChangePayload();
        invoicePaymentChargebackChangePayload
                .setInvoicePaymentChargebackLevyChanged(invoicePaymentChargebackLevyChanged);

        return buildInvoiceChangeChargeback(invoicePaymentChargebackChangePayload);
    }

    public static InvoiceChange buildInvoiceChangeChargebackStageChanged() {
        InvoicePaymentChargebackStageChanged invoicePaymentChargebackStageChanged =
                new InvoicePaymentChargebackStageChanged();
        invoicePaymentChargebackStageChanged.setStage(buildChargebackStage());
        InvoicePaymentChargebackChangePayload invoicePaymentChargebackChangePayload =
                new InvoicePaymentChargebackChangePayload();
        invoicePaymentChargebackChangePayload
                .setInvoicePaymentChargebackStageChanged(invoicePaymentChargebackStageChanged);

        return buildInvoiceChangeChargeback(invoicePaymentChargebackChangePayload);
    }

    public static InvoiceChange buildInvoiceChangeChargebackCashFlowChanged() {
        InvoicePaymentChargebackCashFlowChanged invoicePaymentChargebackCashFlowChanged =
                new InvoicePaymentChargebackCashFlowChanged();
        invoicePaymentChargebackCashFlowChanged.setCashFlow(Collections.singletonList(buildCashFlowPosting()));
        InvoicePaymentChargebackChangePayload invoicePaymentChargebackChangePayload =
                new InvoicePaymentChargebackChangePayload();
        invoicePaymentChargebackChangePayload
                .setInvoicePaymentChargebackCashFlowChanged(invoicePaymentChargebackCashFlowChanged);

        return buildInvoiceChangeChargeback(invoicePaymentChargebackChangePayload);
    }

    public static InvoiceChange buildInvoiceChangeChargebackBodyChanged() {
        InvoicePaymentChargebackBodyChanged invoicePaymentChargebackBodyChanged =
                new InvoicePaymentChargebackBodyChanged();
        Cash cash = new Cash().setAmount(1000).setCurrency(new CurrencyRef("653"));
        invoicePaymentChargebackBodyChanged.setBody(cash);
        InvoicePaymentChargebackChangePayload invoicePaymentChargebackChangePayload =
                new InvoicePaymentChargebackChangePayload();
        invoicePaymentChargebackChangePayload
                .setInvoicePaymentChargebackBodyChanged(invoicePaymentChargebackBodyChanged);

        return buildInvoiceChangeChargeback(invoicePaymentChargebackChangePayload);
    }

    private static InvoiceChange buildInvoiceChangeChargeback(InvoicePaymentChargebackChangePayload payload) {
        InvoicePaymentChargeback invoicePaymentChargeback =
                EnhancedRandom.random(InvoicePaymentChargeback.class, "context", "status", "reason", "stage");
        invoicePaymentChargeback.setCreatedAt(TypeUtil.temporalToString(Instant.now()));
        InvoicePaymentChargebackStatus invoicePaymentChargebackStatus = buildChargebackStatus();
        invoicePaymentChargeback.setStatus(invoicePaymentChargebackStatus);

        InvoicePaymentChargebackReason invoicePaymentChargebackReason = buildChargebackReason();
        invoicePaymentChargeback.setReason(invoicePaymentChargebackReason);

        InvoicePaymentChargebackStage invoicePaymentChargebackStage = buildChargebackStage();
        invoicePaymentChargeback.setStage(invoicePaymentChargebackStage);

        InvoicePaymentChargebackChange invoicePaymentChargebackChange = new InvoicePaymentChargebackChange();
        invoicePaymentChargebackChange.setId("testChargebackId");
        invoicePaymentChargebackChange.setPayload(payload);

        InvoicePaymentChangePayload invoicePaymentChangePayload = new InvoicePaymentChangePayload();
        invoicePaymentChangePayload.setInvoicePaymentChargebackChange(invoicePaymentChargebackChange);

        InvoicePaymentChange invoicePaymentChange = new InvoicePaymentChange();
        invoicePaymentChange.setId("testPaymentId");
        invoicePaymentChange.setPayload(invoicePaymentChangePayload);

        InvoiceChange invoiceChange = new InvoiceChange();
        invoiceChange.setInvoicePaymentChange(invoicePaymentChange);

        return invoiceChange;
    }

    private static FinalCashFlowPosting buildCashFlowPosting() {
        FinalCashFlowAccount sourceFinalCashFlowAccount = new FinalCashFlowAccount();
        sourceFinalCashFlowAccount.setAccountId(12345);
        sourceFinalCashFlowAccount.setAccountType(CashFlowAccount.merchant(MerchantCashFlowAccount.payout));
        FinalCashFlowAccount destFinalCashFlowAccount = new FinalCashFlowAccount();
        destFinalCashFlowAccount.setAccountId(56789);
        destFinalCashFlowAccount.setAccountType(CashFlowAccount.provider(ProviderCashFlowAccount.settlement));
        FinalCashFlowPosting cashFlowPosting = new FinalCashFlowPosting();
        cashFlowPosting.setDetails("testDetails");
        cashFlowPosting.setSource(sourceFinalCashFlowAccount);
        cashFlowPosting.setDestination(destFinalCashFlowAccount);
        Cash cash = new Cash();
        cash.setAmount(1000);
        cash.setCurrency(new CurrencyRef("643"));
        cashFlowPosting.setVolume(cash);

        return cashFlowPosting;
    }

    private static InvoicePaymentChargebackReason buildChargebackReason() {
        InvoicePaymentChargebackReason invoicePaymentChargebackReason = new InvoicePaymentChargebackReason();
        invoicePaymentChargebackReason.setCode("testCode");
        InvoicePaymentChargebackCategory invoicePaymentChargebackCategory = new InvoicePaymentChargebackCategory();
        invoicePaymentChargebackCategory.setFraud(new InvoicePaymentChargebackCategoryFraud());
        invoicePaymentChargebackReason.setCategory(invoicePaymentChargebackCategory);

        return invoicePaymentChargebackReason;
    }

    private static InvoicePaymentChargebackStatus buildChargebackStatus() {
        InvoicePaymentChargebackStatus invoicePaymentChargebackStatus = new InvoicePaymentChargebackStatus();
        invoicePaymentChargebackStatus.setAccepted(new InvoicePaymentChargebackAccepted());

        return invoicePaymentChargebackStatus;
    }

    private static InvoicePaymentChargebackStage buildChargebackStage() {
        InvoicePaymentChargebackStage invoicePaymentChargebackStage = new InvoicePaymentChargebackStage();
        invoicePaymentChargebackStage.setChargeback(new InvoicePaymentChargebackStageChargeback());

        return invoicePaymentChargebackStage;
    }

    public static Contractor buildContractor() {
        Contractor contractor = new Contractor();
        LegalEntity legalEntity = new LegalEntity();
        contractor.setLegalEntity(legalEntity);
        InternationalLegalEntity internationalLegalEntity = new InternationalLegalEntity();
        legalEntity.setInternationalLegalEntity(internationalLegalEntity);
        internationalLegalEntity
                .setCountry(new CountryRef().setId(CountryCode.findByValue(CountryCode.AUT.getValue())));
        internationalLegalEntity.setLegalName(randomString());
        internationalLegalEntity.setActualAddress(randomString());
        internationalLegalEntity.setRegisteredAddress(randomString());
        return contractor;
    }

    public static String randomString() {
        return UUID.randomUUID().toString();
    }

    public static CountryObject buildCountryObject() {
        Country country = new Country();
        country.setName(randomString());
        country.setTradeBlocs(Set.of(new TradeBlocRef().setId(randomString())));
        CountryObject countryObject = new CountryObject();
        countryObject.setData(country);
        countryObject.setRef(new CountryRef().setId(CountryCode.ABH));
        return countryObject;
    }

    public static TradeBlocObject buildTradeBlocObject() {
        TradeBloc tradeBloc = new TradeBloc();
        tradeBloc.setName(randomString());
        tradeBloc.setDescription(randomString());
        TradeBlocObject tradeBlocObject = new TradeBlocObject();
        tradeBlocObject.setData(tradeBloc);
        tradeBlocObject.setRef(new TradeBlocRef().setId(randomString()));
        return tradeBlocObject;
    }

}
