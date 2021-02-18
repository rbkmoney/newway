package com.rbkmoney.newway.poller.event_stock.impl.withdrawal;

import com.rbkmoney.fistful.cashflow.FinalCashFlowPosting;
import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.TimestampedChange;
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
public class WithdrawalTransferCreatedHandler extends AbstractWithdrawalHandler {

    private final WithdrawalDao withdrawalDao;
    private final FistfulCashFlowDao fistfulCashFlowDao;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.transfer.payload.created.transfer.cashflow", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        List<FinalCashFlowPosting> postings = change.getTransfer().getPayload().getCreated().getTransfer().getCashflow().getPostings();
        long sequenceId = event.getEventId();
        String withdrawalId = event.getSourceId();
        log.info("Start withdrawal transfer created handling, sequenceId={}, withdrawalId={}, transfer={}",
                sequenceId, withdrawalId, change.getTransfer());

        Withdrawal withdrawal = withdrawalDao.get(withdrawalId);
        Long oldId = withdrawal.getId();

        initDefaultFields(event, sequenceId, withdrawalId, withdrawal, timestampedChange.getOccuredAt());
        withdrawal.setWithdrawalTransferStatus(WithdrawalTransferStatus.created);

        withdrawal.setFee(FistfulCashFlowUtil.getFistfulFee(postings));
        withdrawal.setProviderFee(FistfulCashFlowUtil.getFistfulProviderFee(postings));

        withdrawalDao.save(withdrawal).ifPresentOrElse(
                id -> {
                    withdrawalDao.updateNotCurrent(oldId);
                    List<FistfulCashFlow> fistfulCashFlows = FistfulCashFlowUtil.convertFistfulCashFlows(postings, id, FistfulCashFlowChangeType.withdrawal);
                    fistfulCashFlowDao.save(fistfulCashFlows);
                    log.info("Withdrawal transfer have been changed, sequenceId={}, withdrawalId={}", sequenceId, withdrawalId);
                },
                () -> log.info("Withdrawal transfer have been changed, sequenceId={}, withdrawalId={}", sequenceId, withdrawalId));
    }

}
