package com.rbkmoney.newway.poller.event.stock.impl.deposit.adjustment;

import com.rbkmoney.fistful.cashflow.FinalCashFlowPosting;
import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.deposit.adjustment.iface.DepositAdjustmentDao;
import com.rbkmoney.newway.dao.withdrawal.iface.FistfulCashFlowDao;
import com.rbkmoney.newway.domain.enums.DepositTransferStatus;
import com.rbkmoney.newway.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.newway.domain.tables.pojos.DepositAdjustment;
import com.rbkmoney.newway.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.newway.poller.event.stock.impl.deposit.AbstractDepositHandler;
import com.rbkmoney.newway.util.FistfulCashFlowUtil;
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
public class DepositAdjustmentTransferCreatedHandler extends AbstractDepositHandler {

    private final DepositAdjustmentDao depositAdjustmentDao;
    private final FistfulCashFlowDao fistfulCashFlowDao;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.adjustment.payload.transfer.payload.created", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        long sequenceId = event.getEventId();
        String depositId = event.getSourceId();
        String adjustmentId = change.getAdjustment().getId();
        log.info("Start deposit adjustment transfer created handling, sequenceId={}, depositId={}", sequenceId,
                depositId);
        DepositAdjustment depositAdjustment = depositAdjustmentDao.get(depositId, adjustmentId);
        List<FinalCashFlowPosting> postings =
                change.getAdjustment().getPayload().getTransfer().getPayload().getCreated().getTransfer().getCashflow()
                        .getPostings();
        initDefaultFieldsAdjustment(event.getCreatedAt(), timestampedChange.getOccuredAt(), sequenceId,
                depositAdjustment);
        depositAdjustment.setTransferStatus(DepositTransferStatus.created);
        depositAdjustment.setFee(FistfulCashFlowUtil.getFistfulFee(postings));
        depositAdjustment.setProviderFee(FistfulCashFlowUtil.getFistfulProviderFee(postings));
        Long oldDepositAdjustmentId = depositAdjustment.getId();
        depositAdjustmentDao.save(depositAdjustment).ifPresentOrElse(
                id -> {
                    depositAdjustmentDao.updateNotCurrent(oldDepositAdjustmentId);
                    List<FistfulCashFlow> fistfulCashFlows = FistfulCashFlowUtil
                            .convertFistfulCashFlows(postings, id, FistfulCashFlowChangeType.deposit_adjustment);
                    fistfulCashFlowDao.save(fistfulCashFlows);
                },
                () -> log.info("Deposit adjustment transfer have been saved, sequenceId={}, depositId={}", sequenceId,
                        depositId)
        );
    }
}
