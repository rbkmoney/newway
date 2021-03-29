package com.rbkmoney.newway.poller.event.stock.impl.withdrawal.session;

import com.rbkmoney.fistful.base.*;
import com.rbkmoney.fistful.withdrawal_session.Change;
import com.rbkmoney.fistful.withdrawal_session.Session;
import com.rbkmoney.fistful.withdrawal_session.TimestampedChange;
import com.rbkmoney.fistful.withdrawal_session.Withdrawal;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.withdrawal.session.iface.WithdrawalSessionDao;
import com.rbkmoney.newway.domain.enums.BankCardPaymentSystem;
import com.rbkmoney.newway.domain.enums.DestinationResourceType;
import com.rbkmoney.newway.domain.tables.pojos.WithdrawalSession;
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
import com.rbkmoney.newway.factory.WithdrawalSessionMachineEventCopyFactoryImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.rbkmoney.newway.domain.enums.WithdrawalSessionStatus.active;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalSessionCreatedHandler implements WithdrawalSessionHandler {

    private final WithdrawalSessionDao withdrawalSessionDao;
    private final MachineEventCopyFactory<WithdrawalSession, String> withdrawalSessionMachineEventCopyFactory;

    @Getter
    private final Filter filter =
            new PathConditionFilter(new PathConditionRule("change.created", new IsNullCondition().not()));

    @Override
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        long sequenceId = event.getEventId();
        String withdrawalSessionId = event.getSourceId();
        log.info("Start withdrawal session created handling, sequenceId={}, withdrawalId={}",
                sequenceId, withdrawalSessionId);

        WithdrawalSession withdrawalSession = withdrawalSessionMachineEventCopyFactory
                .create(event, sequenceId, withdrawalSessionId, timestampedChange.getOccuredAt());

        Session session = change.getCreated();
        withdrawalSession.setProviderId(session.getRoute().getProviderId());
        withdrawalSession.setProviderIdLegacy(session.getProviderLegacy());

        withdrawalSession.setWithdrawalSessionStatus(active);

        Withdrawal withdrawal = session.getWithdrawal();
        withdrawalSession.setWithdrawalId(withdrawal.getId());

        Resource resource = withdrawal.getDestinationResource();
        withdrawalSession.setResourceType(TBaseUtil.unionFieldToEnum(resource, DestinationResourceType.class));
        if (resource.isSetBankCard()) {
            ResourceBankCard resourceBankCard = resource.getBankCard();
            BankCard bankCard = resourceBankCard.getBankCard();
            withdrawalSession.setDestinationCardToken(bankCard.getToken());
            withdrawalSession.setDestinationCardBin(bankCard.getBin());
            withdrawalSession.setDestinationCardMaskedPan(bankCard.getMaskedPan());
            if (bankCard.isSetPaymentSystem()) {
                withdrawalSession.setDestinationCardPaymentSystem(
                        BankCardPaymentSystem.valueOf(bankCard.getPaymentSystem().name()));
            }
            withdrawalSession.setResourceBankCardBankName(bankCard.getBankName());
            if (bankCard.isSetIssuerCountry()) {
                withdrawalSession.setResourceBankCardIssuerCountry(bankCard.getIssuerCountry().toString());
            }
            if (bankCard.isSetCardType()) {
                withdrawalSession.setResourceBankCardType(bankCard.getCardType().toString());
            }
        } else if (resource.isSetCryptoWallet()) {
            ResourceCryptoWallet resourceCryptoWallet = resource.getCryptoWallet();
            CryptoWallet cryptoWallet = resourceCryptoWallet.getCryptoWallet();
            withdrawalSession.setResourceCryptoWalletId(cryptoWallet.getId());
            withdrawalSession.setResourceCryptoWalletType(cryptoWallet.getCurrency().toString());
            if (cryptoWallet.isSetData()) {
                withdrawalSession.setResourceCryptoWalletData(cryptoWallet.getData().getSetField().getFieldName());
            }
        }

        Cash cash = withdrawal.getCash();
        withdrawalSession.setAmount(cash.getAmount());
        withdrawalSession.setCurrencyCode(cash.getCurrency().getSymbolicCode());

        withdrawalSessionDao.save(withdrawalSession).ifPresentOrElse(
                id -> log.info("Withdrawal session created has been saved, sequenceId={}, withdrawalId={}",
                        sequenceId, withdrawalSessionId),
                () -> log.info("Withdrawal session created bound duplicated, sequenceId={}, withdrawalId={}",
                        sequenceId, withdrawalSessionId));

    }

}
