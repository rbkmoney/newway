package com.rbkmoney.newway.handler.event.stock.impl.withdrawal;

import com.rbkmoney.fistful.transfer.Status;
import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.TimestampedChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.withdrawal.iface.FistfulCashFlowDao;
import com.rbkmoney.newway.dao.withdrawal.iface.WithdrawalDao;
import com.rbkmoney.newway.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.newway.domain.enums.WithdrawalTransferStatus;
import com.rbkmoney.newway.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Withdrawal;
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalTransferStatusChangedHandler implements WithdrawalHandler {

    private final WithdrawalDao withdrawalDao;
    private final FistfulCashFlowDao fistfulCashFlowDao;
    private final MachineEventCopyFactory<Withdrawal, String> machineEventCopyFactory;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.transfer.payload.status_changed.status", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        Status status = change.getTransfer().getPayload().getStatusChanged().getStatus();
        long sequenceId = event.getEventId();
        String withdrawalId = event.getSourceId();
        log.info("Start withdrawal transfer status changed handling, sequenceId={}, withdrawalId={} transfer={}",
                sequenceId, withdrawalId, change.getTransfer());

        final Withdrawal withdrawalOld = withdrawalDao.get(withdrawalId);
        Withdrawal withdrawalNew = machineEventCopyFactory
                .create(event, sequenceId, withdrawalId, withdrawalOld, timestampedChange.getOccuredAt());
        withdrawalNew.setWithdrawalTransferStatus(TBaseUtil.unionFieldToEnum(status, WithdrawalTransferStatus.class));

        withdrawalDao.save(withdrawalNew).ifPresentOrElse(
                id -> {
                    Long oldId = withdrawalOld.getId();
                    withdrawalDao.updateNotCurrent(oldId);
                    List<FistfulCashFlow> cashFlows =
                            fistfulCashFlowDao.getByObjId(oldId, FistfulCashFlowChangeType.withdrawal);
                    cashFlows.forEach(pcf -> {
                        pcf.setId(null);
                        pcf.setObjId(id);
                    });
                    fistfulCashFlowDao.save(cashFlows);
                    log.info("Withdrawal transfer status have been changed, sequenceId={}, withdrawalId={}", sequenceId,
                            withdrawalId);
                },
                () -> log.info("Withdrawal transfer have been changed, sequenceId={}, withdrawalId={}", sequenceId,
                        withdrawalId));
    }

}
