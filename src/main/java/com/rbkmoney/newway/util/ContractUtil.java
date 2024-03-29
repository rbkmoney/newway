package com.rbkmoney.newway.util;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.newway.domain.enums.PayoutToolInfo;
import com.rbkmoney.newway.domain.enums.RepresentativeDocument;
import com.rbkmoney.newway.domain.tables.pojos.ContractAdjustment;
import com.rbkmoney.newway.domain.tables.pojos.PayoutTool;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ContractUtil {
    public static List<ContractAdjustment> convertContractAdjustments(Contract contract, long cntrctId) {
        return contract.getAdjustments().stream().map(ca -> convertContractAdjustment(ca, cntrctId))
                .collect(Collectors.toList());
    }

    public static ContractAdjustment convertContractAdjustment(com.rbkmoney.damsel.domain.ContractAdjustment ca,
                                                               long cntrctId) {
        ContractAdjustment adjustment = new ContractAdjustment();
        adjustment.setCntrctId(cntrctId);
        adjustment.setContractAdjustmentId(ca.getId());
        adjustment.setCreatedAt(TypeUtil.stringToLocalDateTime(ca.getCreatedAt()));
        if (ca.isSetValidSince()) {
            adjustment.setValidSince(TypeUtil.stringToLocalDateTime(ca.getValidSince()));
        }
        if (ca.isSetValidUntil()) {
            adjustment.setValidUntil(TypeUtil.stringToLocalDateTime(ca.getValidUntil()));
        }
        adjustment.setTermsId(ca.getTerms().getId());
        return adjustment;
    }

    public static List<PayoutTool> convertPayoutTools(Contract contract, long cntrctId) {
        return contract.getPayoutTools().stream().map(pt -> convertPayoutTool(pt, cntrctId))
                .collect(Collectors.toList());
    }

    public static PayoutTool buildPayoutTool(long cntrctId,
                                             String payoutToolId,
                                             LocalDateTime createdAt,
                                             String currCode,
                                             com.rbkmoney.damsel.domain.PayoutToolInfo payoutToolInfo) {
        PayoutTool payoutTool = new PayoutTool();
        payoutTool.setCntrctId(cntrctId);
        payoutTool.setPayoutToolId(payoutToolId);
        payoutTool.setCreatedAt(createdAt);
        payoutTool.setCurrencyCode(currCode);
        setPayoutToolInfo(payoutTool, payoutToolInfo);
        return payoutTool;
    }

    public static PayoutTool convertPayoutTool(com.rbkmoney.damsel.domain.PayoutTool pt, long cntrctId) {
        return buildPayoutTool(cntrctId, pt.getId(), TypeUtil.stringToLocalDateTime(pt.getCreatedAt()),
                pt.getCurrency().getSymbolicCode(), pt.getPayoutToolInfo());
    }

    public static void setPayoutToolInfo(PayoutTool payoutTool,
                                         com.rbkmoney.damsel.domain.PayoutToolInfo payoutToolInfoSource) {
        PayoutToolInfo payoutToolInfo =
                TypeUtil.toEnumField(payoutToolInfoSource.getSetField().getFieldName(), PayoutToolInfo.class);
        if (payoutToolInfo == null) {
            throw new IllegalArgumentException("Illegal payout tool info: " + payoutToolInfoSource);
        }
        payoutTool.setPayoutToolInfo(payoutToolInfo);
        if (payoutToolInfoSource.isSetRussianBankAccount()) {
            RussianBankAccount russianBankAccount = payoutToolInfoSource.getRussianBankAccount();
            payoutTool.setPayoutToolInfoRussianBankAccount(russianBankAccount.getAccount());
            payoutTool.setPayoutToolInfoRussianBankName(russianBankAccount.getBankName());
            payoutTool.setPayoutToolInfoRussianBankPostAccount(russianBankAccount.getBankPostAccount());
            payoutTool.setPayoutToolInfoRussianBankBik(russianBankAccount.getBankBik());
        } else if (payoutToolInfoSource.isSetInternationalBankAccount()) {
            InternationalBankAccount internationalBankAccount = payoutToolInfoSource.getInternationalBankAccount();
            payoutTool.setPayoutToolInfoInternationalBankNumber(internationalBankAccount.getNumber());
            payoutTool.setPayoutToolInfoInternationalBankAccountHolder(internationalBankAccount.getAccountHolder());
            payoutTool.setPayoutToolInfoInternationalBankIban(internationalBankAccount.getIban());

            if (internationalBankAccount.isSetBank()) {
                InternationalBankDetails bankDetails = internationalBankAccount.getBank();
                payoutTool.setPayoutToolInfoInternationalBankName(bankDetails.getName());
                payoutTool.setPayoutToolInfoInternationalBankAddress(bankDetails.getAddress());
                payoutTool.setPayoutToolInfoInternationalBankBic(bankDetails.getBic());
                payoutTool.setPayoutToolInfoInternationalBankAbaRtn(bankDetails.getAbaRtn());
                payoutTool.setPayoutToolInfoInternationalBankCountryCode(
                        Optional.ofNullable(bankDetails.getCountry())
                                .map(country -> country.toString())
                                .orElse(null)
                );
            }
            if (internationalBankAccount.isSetCorrespondentAccount()) {
                InternationalBankAccount correspondentBankAccount = internationalBankAccount.getCorrespondentAccount();
                payoutTool.setPayoutToolInfoInternationalCorrespondentBankNumber(correspondentBankAccount.getNumber());
                payoutTool.setPayoutToolInfoInternationalCorrespondentBankAccount(
                        correspondentBankAccount.getAccountHolder());
                payoutTool.setPayoutToolInfoInternationalCorrespondentBankIban(correspondentBankAccount.getIban());

                if (correspondentBankAccount.isSetBank()) {
                    InternationalBankDetails correspondentBankDetails = correspondentBankAccount.getBank();
                    payoutTool.setPayoutToolInfoInternationalCorrespondentBankName(correspondentBankDetails.getName());
                    payoutTool.setPayoutToolInfoInternationalCorrespondentBankAddress(
                            correspondentBankDetails.getAddress());
                    payoutTool.setPayoutToolInfoInternationalCorrespondentBankBic(correspondentBankDetails.getBic());
                    payoutTool.setPayoutToolInfoInternationalCorrespondentBankAbaRtn(
                            correspondentBankDetails.getAbaRtn());
                    payoutTool.setPayoutToolInfoInternationalCorrespondentBankCountryCode(
                            Optional.ofNullable(correspondentBankDetails.getCountry())
                                    .map(country -> country.toString())
                                    .orElse(null)
                    );
                }
            }
        } else if (payoutToolInfoSource.isSetWalletInfo()) {
            payoutTool.setPayoutToolInfoWalletInfoWalletId(payoutToolInfoSource.getWalletInfo().getWalletId());
        }
    }

    public static void fillContractLegalAgreementFields(com.rbkmoney.newway.domain.tables.pojos.Contract contract,
                                                        LegalAgreement legalAgreement) {
        contract.setLegalAgreementId(legalAgreement.getLegalAgreementId());
        contract.setLegalAgreementSignedAt(TypeUtil.stringToLocalDateTime(legalAgreement.getSignedAt()));
        if (legalAgreement.isSetValidUntil()) {
            contract.setLegalAgreementValidUntil(TypeUtil.stringToLocalDateTime(legalAgreement.getValidUntil()));
        }
    }

    public static void fillReportPreferences(com.rbkmoney.newway.domain.tables.pojos.Contract contract,
                                             ServiceAcceptanceActPreferences serviceAcceptanceActPreferences) {
        contract.setReportActScheduleId(serviceAcceptanceActPreferences.getSchedule().getId());
        contract.setReportActSignerPosition(serviceAcceptanceActPreferences.getSigner().getPosition());
        contract.setReportActSignerFullName(serviceAcceptanceActPreferences.getSigner().getFullName());
        com.rbkmoney.damsel.domain.RepresentativeDocument representativeDocument =
                serviceAcceptanceActPreferences.getSigner().getDocument();
        RepresentativeDocument reportActSignerDocument =
                TypeUtil.toEnumField(representativeDocument.getSetField().getFieldName(), RepresentativeDocument.class);
        if (reportActSignerDocument == null) {
            throw new IllegalArgumentException("Illegal representative document: " + representativeDocument);
        }
        contract.setReportActSignerDocument(reportActSignerDocument);
        if (representativeDocument.isSetPowerOfAttorney()) {
            contract.setReportActSignerDocPowerOfAttorneyLegalAgreementId(
                    representativeDocument.getPowerOfAttorney().getLegalAgreementId());
            contract.setReportActSignerDocPowerOfAttorneySignedAt(
                    TypeUtil.stringToLocalDateTime(representativeDocument.getPowerOfAttorney().getSignedAt()));
            if (representativeDocument.getPowerOfAttorney().isSetValidUntil()) {
                contract.setReportActSignerDocPowerOfAttorneyValidUntil(
                        TypeUtil.stringToLocalDateTime(representativeDocument.getPowerOfAttorney().getValidUntil()));
            }
        }
    }

    public static void setNullReportPreferences(com.rbkmoney.newway.domain.tables.pojos.Contract contract) {
        contract.setReportActScheduleId(null);
        contract.setReportActSignerPosition(null);
        contract.setReportActSignerFullName(null);
        contract.setReportActSignerDocument(null);
        contract.setReportActSignerDocPowerOfAttorneyLegalAgreementId(null);
        contract.setReportActSignerDocPowerOfAttorneySignedAt(null);
        contract.setReportActSignerDocPowerOfAttorneyValidUntil(null);
    }

}
