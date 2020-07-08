package com.rbkmoney.newway.poller.event_stock.impl.withdrawal;

import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.SinkEvent;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.withdrawal.iface.WithdrawalDao;
import com.rbkmoney.newway.domain.enums.WithdrawalStatus;
import com.rbkmoney.newway.domain.tables.pojos.Withdrawal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WithdrawalCreatedHandler extends AbstractWithdrawalHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WithdrawalDao withdrawalDao;

    private final Filter filter;

    public WithdrawalCreatedHandler(WithdrawalDao withdrawalDao) {
        this.withdrawalDao = withdrawalDao;
        this.filter = new PathConditionFilter(new PathConditionRule("created.withdrawal", new IsNullCondition().not()));
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        var withdrawalDamsel = change.getCreated().getWithdrawal();

        log.info("Start withdrawal created handling, eventId={}, walletId={}", event.getId(), event.getSource());

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setEventId(event.getId());
        withdrawal.setSequenceId(event.getPayload().getSequence());
        withdrawal.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        withdrawal.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        withdrawal.setWithdrawalId(event.getSource());
        withdrawal.setWalletId(withdrawalDamsel.getWalletId());
        withdrawal.setDestinationId(withdrawalDamsel.getDestinationId());
        withdrawal.setExternalId(withdrawalDamsel.getExternalId());

        Cash cash = withdrawalDamsel.getBody();
        withdrawal.setAmount(cash.getAmount());
        withdrawal.setCurrencyCode(cash.getCurrency().getSymbolicCode());
        withdrawal.setWithdrawalStatus(WithdrawalStatus.pending);

        withdrawalDao.updateNotCurrent(event.getSource());
        withdrawalDao.save(withdrawal);
        log.info("Withdrawal have been saved, eventId={}, walletId={}", event.getId(), event.getSource());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }
}
