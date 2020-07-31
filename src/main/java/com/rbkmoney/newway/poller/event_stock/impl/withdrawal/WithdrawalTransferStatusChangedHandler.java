package com.rbkmoney.newway.poller.event_stock.impl.withdrawal;

import com.rbkmoney.fistful.transfer.Status;
import com.rbkmoney.fistful.withdrawal.Change;
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
public class WithdrawalTransferStatusChangedHandler extends AbstractWithdrawalHandler {

    private final WithdrawalDao withdrawalDao;
    private final FistfulCashFlowDao fistfulCashFlowDao;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("transfer.payload.status_changed.status", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(Change change, MachineEvent event) {
        Status status = change.getTransfer().getPayload().getStatusChanged().getStatus();
        long sequenceId = event.getEventId();
        String withdrawalId = event.getSourceId();
        log.info("Start withdrawal transfer status changed handling, sequenceId={}, withdrawalId={} transfer={}",
                sequenceId, withdrawalId, change.getTransfer());

        Withdrawal withdrawal = withdrawalDao.get(withdrawalId);
        Long oldId = withdrawal.getId();
        initDefaultFields(event, sequenceId, withdrawalId, withdrawal);
        withdrawal.setWithdrawalTransferStatus(TBaseUtil.unionFieldToEnum(status, WithdrawalTransferStatus.class));

        withdrawalDao.save(withdrawal).ifPresentOrElse(
                id -> {
                    withdrawalDao.updateNotCurrent(oldId);
                    List<FistfulCashFlow> cashFlows = fistfulCashFlowDao.getByObjId(withdrawal.getId(), FistfulCashFlowChangeType.withdrawal);
                    cashFlows.forEach(pcf -> {
                        pcf.setId(null);
                        pcf.setObjId(id);
                    });
                    fistfulCashFlowDao.save(cashFlows);
                    log.info("Withdrawal transfer status have been changed, sequenceId={}, withdrawalId={}", sequenceId, withdrawalId);
                },
                () -> log.info("Withdrawal transfer have been changed, sequenceId={}, withdrawalId={}", sequenceId, withdrawalId));
    }

}
