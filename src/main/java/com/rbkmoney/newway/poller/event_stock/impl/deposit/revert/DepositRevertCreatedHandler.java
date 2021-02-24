package com.rbkmoney.newway.poller.event_stock.impl.deposit.revert;

import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.deposit_revert.iface.DepositRevertDao;
import com.rbkmoney.newway.domain.enums.DepositRevertStatus;
import com.rbkmoney.newway.domain.tables.pojos.DepositRevert;
import com.rbkmoney.newway.poller.event_stock.impl.deposit.AbstractDepositHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepositRevertCreatedHandler extends AbstractDepositHandler {

    private final DepositRevertDao depositRevertDao;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.revert.payload.created", new IsNullCondition().not()));

    @Override
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        var revert = change.getRevert().getPayload().getCreated().getRevert();
        long sequenceId = event.getEventId();
        String depositId = event.getSourceId();
        log.info("Start deposit revert created handling, sequenceId={}, depositId={}", sequenceId, depositId);
        DepositRevert depositRevert = new DepositRevert();
        initDefaultFieldsRevert(event.getCreatedAt(), timestampedChange.getOccuredAt(), sequenceId, depositRevert);
        depositRevert.setDepositId(depositId);
        depositRevert.setRevertId(revert.getId());
        depositRevert.setSourceId(revert.getSourceId());
        depositRevert.setWalletId(revert.getWalletId());

        Cash cash = revert.getBody();
        depositRevert.setAmount(cash.getAmount());
        depositRevert.setCurrencyCode(cash.getCurrency().getSymbolicCode());
        depositRevert.setStatus(DepositRevertStatus.pending);
        depositRevert.setExternalId(revert.getExternalId());
        depositRevert.setReason(revert.getReason());

        depositRevertDao.save(depositRevert).ifPresentOrElse(
                dbContractId -> log.info("Deposit revert created has been saved, sequenceId={}, depositId={}",
                        sequenceId, depositId),
                () -> log.info("Deposit revert created bound duplicated, sequenceId={}, depositId={}",
                        sequenceId, depositId));
    }
}
