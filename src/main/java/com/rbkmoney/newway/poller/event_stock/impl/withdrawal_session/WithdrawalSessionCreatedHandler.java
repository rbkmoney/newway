package com.rbkmoney.newway.poller.event_stock.impl.withdrawal_session;

import com.rbkmoney.fistful.base.*;
import com.rbkmoney.fistful.withdrawal_session.Change;
import com.rbkmoney.fistful.withdrawal_session.Session;
import com.rbkmoney.fistful.withdrawal_session.SinkEvent;
import com.rbkmoney.fistful.withdrawal_session.Withdrawal;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.withdrawal_session.iface.WithdrawalSessionDao;
import com.rbkmoney.newway.domain.enums.BankCardPaymentSystem;
import com.rbkmoney.newway.domain.enums.DestinationResourceType;
import com.rbkmoney.newway.domain.tables.pojos.WithdrawalSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.rbkmoney.newway.domain.enums.WithdrawalSessionStatus.active;

@Component
public class WithdrawalSessionCreatedHandler extends AbstractWithdrawalSessionHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WithdrawalSessionDao withdrawalSessionDao;

    private final Filter filter;

    public WithdrawalSessionCreatedHandler(WithdrawalSessionDao withdrawalSessionDao) {
        this.withdrawalSessionDao = withdrawalSessionDao;
        this.filter = new PathConditionFilter(new PathConditionRule("created", new IsNullCondition().not()));
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        log.info("Start withdrawal session created handling (eventId={}, sessionId={})", event.getId(), event.getSource());

        WithdrawalSession withdrawalSession = new WithdrawalSession();
        withdrawalSession.setEventId(event.getId());
        withdrawalSession.setSequenceId(event.getPayload().getSequence());
        withdrawalSession.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        withdrawalSession.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));

        Session session = change.getCreated();
        withdrawalSession.setWithdrawalSessionId(event.getSource());
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
            withdrawalSession.setDestinationCardPaymentSystem(BankCardPaymentSystem.valueOf(bankCard.getPaymentSystem().name()));
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

        withdrawalSessionDao.updateNotCurrent(event.getSource());
        Long id = withdrawalSessionDao.save(withdrawalSession);

        log.info("Withdrawal session have been saved: id={}, eventId={}, sessionId={}",
                id, event.getId(), event.getSource());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }
}
