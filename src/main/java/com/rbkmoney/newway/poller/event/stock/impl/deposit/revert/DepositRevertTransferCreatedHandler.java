package com.rbkmoney.newway.poller.event.stock.impl.deposit.revert;

import com.rbkmoney.fistful.cashflow.FinalCashFlowPosting;
import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.deposit.revert.iface.DepositRevertDao;
import com.rbkmoney.newway.dao.withdrawal.iface.FistfulCashFlowDao;
import com.rbkmoney.newway.domain.enums.DepositTransferStatus;
import com.rbkmoney.newway.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.newway.domain.tables.pojos.DepositRevert;
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
public class DepositRevertTransferCreatedHandler extends AbstractDepositHandler {

    private final DepositRevertDao depositRevertDao;
    private final FistfulCashFlowDao fistfulCashFlowDao;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.revert.payload.transfer.payload.created", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        long sequenceId = event.getEventId();
        String depositId = event.getSourceId();
        String revertId = change.getRevert().getId();
        log.info("Start deposit revert transfer created handling, sequenceId={}, depositId={}", sequenceId, depositId);
        DepositRevert depositRevert = depositRevertDao.get(depositId, revertId);
        Long oldDepositRevertId = depositRevert.getId();
        initDefaultFieldsRevert(event.getCreatedAt(), timestampedChange.getOccuredAt(), sequenceId, depositRevert);
        List<FinalCashFlowPosting> postings =
                change.getRevert().getPayload().getTransfer().getPayload().getCreated().getTransfer().getCashflow()
                        .getPostings();
        depositRevert.setTransferStatus(DepositTransferStatus.created);
        depositRevert.setFee(FistfulCashFlowUtil.getFistfulFee(postings));
        depositRevert.setProviderFee(FistfulCashFlowUtil.getFistfulProviderFee(postings));

        depositRevertDao.save(depositRevert).ifPresentOrElse(
                id -> {
                    depositRevertDao.updateNotCurrent(oldDepositRevertId);
                    List<FistfulCashFlow> fistfulCashFlows = FistfulCashFlowUtil
                            .convertFistfulCashFlows(postings, id, FistfulCashFlowChangeType.deposit_revert);
                    fistfulCashFlowDao.save(fistfulCashFlows);
                },
                () -> log.info("Deposit revert transfer have been saved, sequenceId={}, depositId={}", sequenceId,
                        depositId)
        );
    }

}
