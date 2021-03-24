package com.rbkmoney.newway.poller.event_stock.impl.deposit.adjustment;

import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.fistful.deposit.adjustment.Status;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.deposit_adjustment.iface.DepositAdjustmentDao;
import com.rbkmoney.newway.dao.withdrawal.iface.FistfulCashFlowDao;
import com.rbkmoney.newway.domain.enums.DepositAdjustmentStatus;
import com.rbkmoney.newway.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.newway.domain.tables.pojos.DepositAdjustment;
import com.rbkmoney.newway.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.newway.poller.event_stock.impl.deposit.AbstractDepositHandler;
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
public class DepositAdjustmentStatusChangedHandler extends AbstractDepositHandler {

    private final DepositAdjustmentDao depositAdjustmentDao;
    private final FistfulCashFlowDao fistfulCashFlowDao;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.adjustment.payload.status_changed.status", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        Status status = change.getAdjustment().getPayload().getStatusChanged().getStatus();
        long sequenceId = event.getEventId();
        String depositId = event.getSourceId();
        String adjustmentId = change.getAdjustment().getId();
        log.info("Start deposit adjustment status changed handling, sequenceId={}, depositId={}", sequenceId, depositId);
        DepositAdjustment depositAdjustment = depositAdjustmentDao.get(depositId, adjustmentId);
        Long oldDepositAdjustmentId = depositAdjustment.getId();
        initDefaultFieldsAdjustment(event.getCreatedAt(), timestampedChange.getOccuredAt(), sequenceId, depositAdjustment);
        depositAdjustment.setStatus(TBaseUtil.unionFieldToEnum(status, DepositAdjustmentStatus.class));

        depositAdjustmentDao.save(depositAdjustment).ifPresentOrElse(
                id -> {
                    depositAdjustmentDao.updateNotCurrent(oldDepositAdjustmentId);
                    List<FistfulCashFlow> cashFlows = fistfulCashFlowDao.getByObjId(depositAdjustment.getId(), FistfulCashFlowChangeType.deposit_adjustment);
                    cashFlows.forEach(pcf -> {
                        pcf.setId(null);
                        pcf.setObjId(id);
                    });
                    fistfulCashFlowDao.save(cashFlows);
                },
                () -> log.info("Deposit adjustment status have been changed, sequenceId={}, depositId={}",
                        sequenceId, depositId)
        );
    }

}
