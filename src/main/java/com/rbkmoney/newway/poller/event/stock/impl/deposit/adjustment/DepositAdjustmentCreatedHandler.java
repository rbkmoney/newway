package com.rbkmoney.newway.poller.event.stock.impl.deposit.adjustment;

import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.fistful.deposit.adjustment.CashFlowChangePlan;
import com.rbkmoney.fistful.deposit.status.Status;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.deposit.adjustment.iface.DepositAdjustmentDao;
import com.rbkmoney.newway.dao.deposit.iface.DepositDao;
import com.rbkmoney.newway.domain.enums.DepositAdjustmentStatus;
import com.rbkmoney.newway.domain.enums.DepositStatus;
import com.rbkmoney.newway.domain.tables.pojos.Deposit;
import com.rbkmoney.newway.domain.tables.pojos.DepositAdjustment;
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
import com.rbkmoney.newway.poller.event.stock.impl.deposit.DepositHandler;
import com.rbkmoney.newway.util.FistfulCashFlowUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepositAdjustmentCreatedHandler implements DepositHandler {

    private final DepositAdjustmentDao depositAdjustmentDao;
    private final DepositDao depositDao;
    private final MachineEventCopyFactory<DepositAdjustment, String> depositRevertMachineEventCopyFactory;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.adjustment.payload.created.adjustment", new IsNullCondition().not()));

    @Override
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        long sequenceId = event.getEventId();
        String depositId = event.getSourceId();
        log.info("Start deposit adjustment created handling, sequenceId={}, depositId={}", sequenceId, depositId);

        var adjustment = change.getAdjustment().getPayload().getCreated().getAdjustment();
        Deposit deposit = depositDao.get(depositId);
        DepositAdjustment depositAdjustment = depositRevertMachineEventCopyFactory
                .create(event, sequenceId, depositId, timestampedChange.getOccuredAt());

        depositAdjustment.setAdjustmentId(adjustment.getId());
        depositAdjustment.setWalletId(deposit.getWalletId());
        depositAdjustment.setSourceId(deposit.getSourceId());

        if (adjustment.getChangesPlan().isSetNewCashFlow()) {
            CashFlowChangePlan cashFlow = adjustment.getChangesPlan().getNewCashFlow();
            long amount = computeAmount(cashFlow);
            depositAdjustment.setAmount(amount);
            String currCode = getSymbolicCode(cashFlow);
            depositAdjustment.setCurrencyCode(currCode);
            depositAdjustment
                    .setProviderFee(FistfulCashFlowUtil.getFistfulProviderFee(cashFlow.getNewCashFlow().getPostings()));
            depositAdjustment.setFee(FistfulCashFlowUtil.getFistfulFee(cashFlow.getNewCashFlow().getPostings()));
        }
        if (adjustment.getChangesPlan().isSetNewStatus()) {
            Status status = adjustment.getChangesPlan().getNewStatus().getNewStatus();
            depositAdjustment.setDepositStatus(TBaseUtil.unionFieldToEnum(status, DepositStatus.class));
        }

        depositAdjustment.setStatus(DepositAdjustmentStatus.pending);
        depositAdjustment.setExternalId(adjustment.getExternalId());
        depositAdjustment.setPartyRevision(adjustment.getPartyRevision());
        depositAdjustment.setDomainRevision(adjustment.getDomainRevision());

        depositAdjustmentDao.save(depositAdjustment).ifPresentOrElse(
                dbContractId -> log.info("Deposit adjustment created has been saved, sequenceId={}, depositId={}",
                        sequenceId, depositId),
                () -> log.info("Deposit adjustment created bound duplicated, sequenceId={}, depositId={}",
                        sequenceId, depositId));
    }

    private String getSymbolicCode(CashFlowChangePlan cashFlow) {
        return cashFlow.getNewCashFlow().getPostings().get(0).getVolume().getCurrency().getSymbolicCode();
    }

    private long computeAmount(CashFlowChangePlan cashFlow) {
        Long oldAmount = FistfulCashFlowUtil.computeAmount(cashFlow.getOldCashFlowInverted().getPostings());
        Long newAmount = FistfulCashFlowUtil.computeAmount(cashFlow.getNewCashFlow().getPostings());
        return newAmount + oldAmount;
    }
}
