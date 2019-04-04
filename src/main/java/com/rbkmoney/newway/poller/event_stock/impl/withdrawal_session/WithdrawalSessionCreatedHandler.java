package com.rbkmoney.newway.poller.event_stock.impl.withdrawal_session;

import com.rbkmoney.fistful.base.BankCard;
import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.withdrawal_session.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.withdrawal_session.iface.WithdrawalSessionDao;
import com.rbkmoney.newway.domain.enums.BankCardPaymentSystem;
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
        withdrawalSession.setProviderId(session.getProvider());
        withdrawalSession.setWithdrawalSessionStatus(active);

        Withdrawal withdrawal = session.getWithdrawal();
        withdrawalSession.setWithdrawalId(withdrawal.getId());
        withdrawalSession.setDestinationName(withdrawal.getDestination().getName());

        BankCard bankCard = withdrawal.getDestination().getResource().getBankCard();
        withdrawalSession.setDestinationCardToken(bankCard.getToken());
        withdrawalSession.setDestinationCardBin(bankCard.getBin());
        withdrawalSession.setDestinationCardMaskedPan(bankCard.getMaskedPan());
        withdrawalSession.setDestinationCardPaymentSystem(BankCardPaymentSystem.valueOf(bankCard.getPaymentSystem().name()));

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
