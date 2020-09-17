package com.rbkmoney.newway.poller.event_stock.impl.deposit;

import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.deposit.iface.DepositDao;
import com.rbkmoney.newway.domain.enums.DepositStatus;
import com.rbkmoney.newway.domain.tables.pojos.Deposit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepositCreatedHandler extends AbstractDepositHandler {

    private final DepositDao depositDao;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.created.deposit", new IsNullCondition().not()));

    @Override
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        var depositDamsel = change.getCreated().getDeposit();
        long sequenceId = event.getEventId();
        String depositId = event.getSourceId();
        log.info("Start deposit created handling, sequenceId={}, depositId={}", sequenceId, depositId);
        Deposit deposit = new Deposit();
        initDefaultFieldsDeposit(event, sequenceId, depositId, deposit, timestampedChange.getOccuredAt());
        deposit.setSourceId(depositDamsel.getSourceId());
        deposit.setWalletId(depositDamsel.getWalletId());

        Cash cash = depositDamsel.getBody();
        deposit.setAmount(cash.getAmount());
        deposit.setCurrencyCode(cash.getCurrency().getSymbolicCode());
        deposit.setDepositStatus(DepositStatus.pending);
        deposit.setExternalId(depositDamsel.getExternalId());

        depositDao.save(deposit).ifPresentOrElse(
                dbContractId -> log.info("Deposit created has been saved, sequenceId={}, depositId={}",
                        sequenceId, depositId),
                () -> log.info("Deposit created bound duplicated, sequenceId={}, depositId={}",
                        sequenceId, depositId));
    }

}
