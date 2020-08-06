package com.rbkmoney.newway.poller.event_stock.impl.deposit;

import com.rbkmoney.fistful.cashflow.FinalCashFlowPosting;
import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.deposit.iface.DepositDao;
import com.rbkmoney.newway.dao.withdrawal.iface.FistfulCashFlowDao;
import com.rbkmoney.newway.domain.enums.DepositTransferStatus;
import com.rbkmoney.newway.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.newway.domain.tables.pojos.Deposit;
import com.rbkmoney.newway.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.newway.util.CashFlowUtil;
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
public class DepositTransferCreatedHandler extends AbstractDepositHandler {

    private final DepositDao depositDao;
    private final FistfulCashFlowDao fistfulCashFlowDao;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("transfer.payload.created.transfer.cashflow", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        List<FinalCashFlowPosting> postings = change.getTransfer().getPayload().getCreated().getTransfer().getCashflow().getPostings();
        long sequenceId = event.getEventId();
        String depositId = event.getSourceId();
        log.info("Start deposit transfer created handling, sequenceId={}, depositId={}", sequenceId, depositId);
        Deposit deposit = depositDao.get(depositId);
        Long oldId = deposit.getId();
        initDefaultFieldsDeposit(event, sequenceId, depositId, deposit, timestampedChange.getOccuredAt());
        deposit.setDepositTransferStatus(DepositTransferStatus.created);
        deposit.setFee(CashFlowUtil.getFistfulFee(postings));
        deposit.setProviderFee(CashFlowUtil.getFistfulProviderFee(postings));
        depositDao.save(deposit).ifPresentOrElse(
                id -> {
                    depositDao.updateNotCurrent(oldId);
                    List<FistfulCashFlow> fistfulCashFlows = CashFlowUtil.convertFistfulCashFlows(postings, id, FistfulCashFlowChangeType.deposit);
                    fistfulCashFlowDao.save(fistfulCashFlows);
                },
                () -> log.info("Deposit transfer have been saved, sequenceId={}, depositId={}", sequenceId, depositId)
        );
    }

}
