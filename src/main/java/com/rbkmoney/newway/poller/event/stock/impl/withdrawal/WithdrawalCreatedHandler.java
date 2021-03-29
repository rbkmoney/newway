package com.rbkmoney.newway.poller.event.stock.impl.withdrawal;

import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.TimestampedChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.withdrawal.iface.WithdrawalDao;
import com.rbkmoney.newway.domain.enums.WithdrawalStatus;
import com.rbkmoney.newway.domain.tables.pojos.Withdrawal;
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalCreatedHandler implements WithdrawalHandler {

    private final WithdrawalDao withdrawalDao;
    private final MachineEventCopyFactory<Withdrawal, String> machineEventCopyFactory;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.created.withdrawal", new IsNullCondition().not()));

    @Override
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        var withdrawalDamsel = change.getCreated().getWithdrawal();
        long sequenceId = event.getEventId();
        String withdrawalId = event.getSourceId();
        log.info("Start withdrawal created handling, sequenceId={}, withdrawalId={}", sequenceId, withdrawalId);

        Withdrawal withdrawal =
                machineEventCopyFactory.create(event, sequenceId, withdrawalId, timestampedChange.getOccuredAt());

        withdrawal.setWalletId(withdrawalDamsel.getWalletId());
        withdrawal.setDestinationId(withdrawalDamsel.getDestinationId());
        withdrawal.setExternalId(withdrawalDamsel.getExternalId());

        Cash cash = withdrawalDamsel.getBody();
        withdrawal.setAmount(cash.getAmount());
        withdrawal.setCurrencyCode(cash.getCurrency().getSymbolicCode());
        withdrawal.setWithdrawalStatus(WithdrawalStatus.pending);

        withdrawalDao.save(withdrawal).ifPresentOrElse(
                dbContractId -> log
                        .info("Withdrawal created has been saved, sequenceId={}, withdrawalId={}", sequenceId,
                                withdrawalId),
                () -> log.info("Withdrawal created bound duplicated, sequenceId={}, withdrawalId={}", sequenceId,
                        withdrawalId));
    }

}
