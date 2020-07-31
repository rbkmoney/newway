package com.rbkmoney.newway.poller.event_stock.impl.withdrawal;

import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.withdrawal.iface.WithdrawalDao;
import com.rbkmoney.newway.domain.enums.WithdrawalStatus;
import com.rbkmoney.newway.domain.tables.pojos.Withdrawal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalCreatedHandler extends AbstractWithdrawalHandler {

    private final WithdrawalDao withdrawalDao;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("created.withdrawal", new IsNullCondition().not()));

    @Override
    public void handle(Change change, MachineEvent event) {
        var withdrawalDamsel = change.getCreated().getWithdrawal();
        long sequenceId = event.getEventId();
        String withdrawalId = event.getSourceId();
        log.info("Start withdrawal created handling, sequenceId={}, withdrawalId={}", sequenceId, withdrawalId);

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setSequenceId((int) sequenceId);
        withdrawal.setWithdrawalId(withdrawalId);
        withdrawal.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        withdrawal.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        withdrawal.setWalletId(withdrawalDamsel.getWalletId());
        withdrawal.setDestinationId(withdrawalDamsel.getDestinationId());
        withdrawal.setExternalId(withdrawalDamsel.getExternalId());

        Cash cash = withdrawalDamsel.getBody();
        withdrawal.setAmount(cash.getAmount());
        withdrawal.setCurrencyCode(cash.getCurrency().getSymbolicCode());
        withdrawal.setWithdrawalStatus(WithdrawalStatus.pending);

        withdrawalDao.save(withdrawal).ifPresentOrElse(
                dbContractId -> log.info("Withdrawal created has been saved, sequenceId={}, withdrawalId={}", sequenceId, withdrawalId),
                () -> log.info("Withdrawal created bound duplicated, sequenceId={}, withdrawalId={}", sequenceId, withdrawalId));
    }

}
