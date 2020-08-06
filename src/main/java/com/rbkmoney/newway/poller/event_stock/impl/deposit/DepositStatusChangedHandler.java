package com.rbkmoney.newway.poller.event_stock.impl.deposit;

import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.fistful.deposit.status.Status;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.deposit.iface.DepositDao;
import com.rbkmoney.newway.dao.withdrawal.iface.FistfulCashFlowDao;
import com.rbkmoney.newway.domain.enums.DepositStatus;
import com.rbkmoney.newway.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.newway.domain.tables.pojos.Deposit;
import com.rbkmoney.newway.domain.tables.pojos.FistfulCashFlow;
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
public class DepositStatusChangedHandler extends AbstractDepositHandler {

    private final DepositDao depositDao;
    private final FistfulCashFlowDao fistfulCashFlowDao;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("status_changed.status", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        Status status = change.getStatusChanged().getStatus();
        long sequenceId = event.getEventId();
        String depositId = event.getSourceId();
        log.info("Start deposit status changed handling, sequenceId={}, depositId={}", sequenceId, depositId);
        Deposit deposit = depositDao.get(depositId);
        Long oldDepositId = deposit.getId();
        initDefaultFieldsDeposit(event, sequenceId, depositId, deposit, timestampedChange.getOccuredAt());
        deposit.setDepositStatus(TBaseUtil.unionFieldToEnum(status, DepositStatus.class));

        depositDao.save(deposit).ifPresentOrElse(
                id -> {
                    depositDao.updateNotCurrent(oldDepositId);
                    List<FistfulCashFlow> cashFlows = fistfulCashFlowDao.getByObjId(deposit.getId(), FistfulCashFlowChangeType.deposit);
                    cashFlows.forEach(pcf -> {
                        pcf.setId(null);
                        pcf.setObjId(id);
                    });
                    fistfulCashFlowDao.save(cashFlows);
                },
                () -> log.info("Deposit status have been changed, sequenceId={}, depositId={}",
                        sequenceId, depositId)
        );
    }

}
