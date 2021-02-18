package com.rbkmoney.newway.poller.event_stock.impl.deposit.revert;

import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.fistful.transfer.Status;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.deposit_revert.iface.DepositRevertDao;
import com.rbkmoney.newway.dao.withdrawal.iface.FistfulCashFlowDao;
import com.rbkmoney.newway.domain.enums.DepositTransferStatus;
import com.rbkmoney.newway.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.newway.domain.tables.pojos.DepositRevert;
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
public class DepositRevertTransferStatusChangedHandler extends AbstractDepositHandler {

    private final DepositRevertDao depositRevertDao;
    private final FistfulCashFlowDao fistfulCashFlowDao;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.revert.payload.transfer.payload.status_changed.status", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        Status status = change.getRevert().getPayload().getTransfer().getPayload().getStatusChanged().getStatus();
        long sequenceId = event.getEventId();
        String depositId = event.getSourceId();
        String revertId = change.getRevert().getId();
        log.info("Start deposit revert transfer status changed handling, sequenceId={}, depositId={}", sequenceId, depositId);
        DepositRevert depositRevert = depositRevertDao.get(depositId, revertId);
        Long oldDepositRevertId = depositRevert.getId();
        initDefaultFieldsRevert(event.getCreatedAt(), timestampedChange.getOccuredAt(), sequenceId, depositRevert);
        depositRevert.setTransferStatus(TBaseUtil.unionFieldToEnum(status, DepositTransferStatus.class));

        depositRevertDao.save(depositRevert).ifPresentOrElse(
                id -> {
                    depositRevertDao.updateNotCurrent(oldDepositRevertId);
                    List<FistfulCashFlow> cashFlows = fistfulCashFlowDao.getByObjId(depositRevert.getId(), FistfulCashFlowChangeType.deposit_revert);
                    cashFlows.forEach(pcf -> {
                        pcf.setId(null);
                        pcf.setObjId(id);
                    });
                    fistfulCashFlowDao.save(cashFlows);
                },
                () -> log.info("Deposit revert transfer status have been changed, sequenceId={}, depositId={}",
                        sequenceId, depositId)
        );
    }

}
