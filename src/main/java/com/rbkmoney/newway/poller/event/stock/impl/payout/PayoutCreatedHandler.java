package com.rbkmoney.newway.poller.event.stock.impl.payout;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payout_processing.*;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.payout.iface.PayoutDao;
import com.rbkmoney.newway.dao.payout.iface.PayoutSummaryDao;
import com.rbkmoney.newway.domain.enums.PayoutStatus;
import com.rbkmoney.newway.domain.enums.PayoutType;
import com.rbkmoney.newway.domain.enums.*;
import com.rbkmoney.newway.domain.tables.pojos.Payout;
import com.rbkmoney.newway.domain.tables.pojos.PayoutSummary;
import com.rbkmoney.newway.util.PayoutUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayoutCreatedHandler extends AbstractPayoutHandler {

    private final PayoutDao payoutDao;
    private final PayoutSummaryDao payoutSummaryDao;

    @Getter
    private final Filter filter = new PathConditionFilter(new PathConditionRule(
            "payout_created",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PayoutChange change, Event event, Integer changeId) {
        long eventId = event.getId();
        com.rbkmoney.damsel.payout_processing.Payout payoutCreated = change.getPayoutCreated().getPayout();
        String payoutId = payoutCreated.getId();
        String partyId = payoutCreated.getPartyId();
        log.info("Start payout created handling, eventId={}, partyId={}, payoutId={}", eventId, partyId, payoutId);
        Payout payout = new Payout();
        payout.setEventId(eventId);
        payout.setChangeId(changeId);
        payout.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        payout.setPayoutId(payoutCreated.getId());
        payout.setPartyId(partyId);
        payout.setShopId(payoutCreated.getShopId());
        payout.setContractId(payoutCreated.getContractId());
        payout.setCreatedAt(TypeUtil.stringToLocalDateTime(payoutCreated.getCreatedAt()));
        payout.setStatus(TBaseUtil.unionFieldToEnum(payoutCreated.getStatus(), PayoutStatus.class));
        payout.setAmount(payoutCreated.getAmount());
        payout.setFee(payoutCreated.getFee());
        payout.setCurrencyCode(payoutCreated.getCurrency().getSymbolicCode());
        if (payoutCreated.getStatus().isSetCancelled()) {
            PayoutCancelled cancelled = payoutCreated.getStatus().getCancelled();
            payout.setStatusCancelledDetails(cancelled.getDetails());
        }
        PayoutType payoutType = TBaseUtil.unionFieldToEnum(payoutCreated.getType(), PayoutType.class);
        payout.setType(payoutType);
        if (payoutCreated.getType().isSetWallet()) {
            String walletId = payoutCreated.getType().getWallet().getWalletId();
            payout.setWalletId(walletId);
        } else if (payoutCreated.getType().isSetBankAccount()) {
            PayoutAccount payoutAccount = payoutCreated.getType().getBankAccount();
            payout.setTypeAccountType(TBaseUtil.unionFieldToEnum(payoutAccount, PayoutAccountType.class));
            if (payoutAccount.isSetRussianPayoutAccount()) {
                RussianPayoutAccount russianPayoutAccount = payoutAccount.getRussianPayoutAccount();
                RussianBankAccount russianBankAccount = russianPayoutAccount.getBankAccount();
                payout.setTypeAccountRussianAccount(russianBankAccount.getAccount());
                payout.setTypeAccountRussianBankName(russianBankAccount.getBankName());
                payout.setTypeAccountRussianBankPostAccount(russianBankAccount.getBankPostAccount());
                payout.setTypeAccountRussianBankBik(russianBankAccount.getBankBik());
                payout.setTypeAccountRussianInn(russianPayoutAccount.getInn());
                payout.setTypeAccountPurpose(russianPayoutAccount.getPurpose());
                payout.setTypeAccountLegalAgreementSignedAt(
                        TypeUtil.stringToLocalDateTime(russianPayoutAccount.getLegalAgreement().getSignedAt()));
                payout.setTypeAccountLegalAgreementId(russianPayoutAccount.getLegalAgreement().getLegalAgreementId());
                if (russianPayoutAccount.getLegalAgreement().isSetValidUntil()) {
                    payout.setTypeAccountLegalAgreementValidUntil(
                            TypeUtil.stringToLocalDateTime(russianPayoutAccount.getLegalAgreement().getValidUntil()));
                }
            } else if (payoutAccount.isSetInternationalPayoutAccount()) {
                InternationalPayoutAccount internationalPayoutAccount = payoutAccount.getInternationalPayoutAccount();
                InternationalBankAccount bankAccount = internationalPayoutAccount.getBankAccount();
                payout.setTypeAccountInternationalAccountHolder(bankAccount.getAccountHolder());
                payout.setTypeAccountInternationalIban(bankAccount.getIban());
                payout.setTypeAccountInternationalBankNumber(bankAccount.getNumber());

                if (bankAccount.isSetBank()) {
                    InternationalBankDetails bankDetails = bankAccount.getBank();
                    payout.setTypeAccountInternationalBankName(bankDetails.getName());
                    payout.setTypeAccountInternationalBankAddress(bankDetails.getAddress());
                    payout.setTypeAccountInternationalBic(bankDetails.getBic());
                    payout.setTypeAccountInternationalBankAbaRtn(bankDetails.getAbaRtn());
                    payout.setTypeAccountInternationalBankCountryCode(
                            Optional.ofNullable(bankDetails.getCountry())
                                    .map(Enum::toString)
                                    .orElse(null)
                    );
                }

                if (bankAccount.isSetCorrespondentAccount()) {
                    InternationalBankAccount correspondentBankAccount = bankAccount.getCorrespondentAccount();
                    payout.setTypeAccountInternationalCorrespondentBankAccount(
                            correspondentBankAccount.getAccountHolder());
                    payout.setTypeAccountInternationalCorrespondentBankIban(correspondentBankAccount.getIban());
                    payout.setTypeAccountInternationalCorrespondentBankNumber(correspondentBankAccount.getNumber());

                    if (correspondentBankAccount.isSetBank()) {
                        InternationalBankDetails correspondentBankDetails = correspondentBankAccount.getBank();
                        payout.setTypeAccountInternationalCorrespondentBankName(correspondentBankDetails.getName());
                        payout.setTypeAccountInternationalCorrespondentBankAddress(
                                correspondentBankDetails.getAddress());
                        payout.setTypeAccountInternationalCorrespondentBankBic(correspondentBankDetails.getBic());
                        payout.setTypeAccountInternationalCorrespondentBankAbaRtn(correspondentBankDetails.getAbaRtn());
                        payout.setTypeAccountInternationalCorrespondentBankCountryCode(
                                Optional.ofNullable(correspondentBankDetails.getCountry())
                                        .map(Enum::toString)
                                        .orElse(null)
                        );
                    }
                }

                payout.setTypeAccountInternationalLegalEntityLegalName(
                        internationalPayoutAccount.getLegalEntity().getLegalName());
                payout.setTypeAccountInternationalLegalEntityTradingName(
                        internationalPayoutAccount.getLegalEntity().getTradingName());
                payout.setTypeAccountInternationalLegalEntityRegisteredAddress(
                        internationalPayoutAccount.getLegalEntity().getRegisteredAddress());
                payout.setTypeAccountInternationalLegalEntityActualAddress(
                        internationalPayoutAccount.getLegalEntity().getActualAddress());
                payout.setTypeAccountInternationalLegalEntityRegisteredNumber(
                        internationalPayoutAccount.getLegalEntity().getRegisteredNumber());
                payout.setTypeAccountPurpose(internationalPayoutAccount.getPurpose());
                payout.setTypeAccountLegalAgreementSignedAt(
                        TypeUtil.stringToLocalDateTime(internationalPayoutAccount.getLegalAgreement().getSignedAt()));
                payout.setTypeAccountLegalAgreementId(
                        internationalPayoutAccount.getLegalAgreement().getLegalAgreementId());
                if (internationalPayoutAccount.getLegalAgreement().isSetValidUntil()) {
                    payout.setTypeAccountLegalAgreementValidUntil(TypeUtil.stringToLocalDateTime(
                            internationalPayoutAccount.getLegalAgreement().getValidUntil()));
                }
            }
        }

        payoutDao.save(payout).ifPresentOrElse(
                id -> {
                    if (payoutCreated.isSetSummary()) {
                        List<PayoutSummary> payoutSummaries = PayoutUtil.convertPayoutSummaries(payoutCreated, id);
                        payoutSummaryDao.save(payoutSummaries);
                    }
                    log.info("Payout has been saved, eventId={}, changeId={}, payoutId={}", eventId, changeId,
                            payoutId);
                },
                () -> log.info("Payout has been bound duplicated, eventId={}, changeId={}, payoutId={}",
                        eventId, changeId, payoutId));
    }

}
