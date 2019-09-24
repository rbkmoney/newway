package com.rbkmoney.newway.poller.event_stock.impl.withdrawal;

import com.rbkmoney.fistful.transfer.Status;
import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.SinkEvent;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.withdrawal.iface.FistfulCashFlowDao;
import com.rbkmoney.newway.dao.withdrawal.iface.WithdrawalDao;
import com.rbkmoney.newway.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.newway.domain.enums.WithdrawalTransferStatus;
import com.rbkmoney.newway.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Withdrawal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class WithdrawalTransferStatusChangedHandler extends AbstractWithdrawalHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WithdrawalDao withdrawalDao;

    private final FistfulCashFlowDao fistfulCashFlowDao;

    private final Filter filter;

    public WithdrawalTransferStatusChangedHandler(WithdrawalDao withdrawalDao, FistfulCashFlowDao fistfulCashFlowDao) {
        this.withdrawalDao = withdrawalDao;
        this.fistfulCashFlowDao = fistfulCashFlowDao;
        this.filter = new PathConditionFilter(new PathConditionRule("transfer.payload.status_changed.status", new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(Change change, SinkEvent event) {
        Status status = change.getTransfer().getPayload().getStatusChanged().getStatus();

        log.info("Start withdrawal transfer status changed handling, eventId={}, walletId={}, transferChange={}", event.getId(), event.getSource(), change.getTransfer());

        Withdrawal withdrawal = withdrawalDao.get(event.getSource());

        withdrawal.setId(null);
        withdrawal.setWtime(null);
        withdrawal.setEventId(event.getId());
        withdrawal.setSequenceId(event.getPayload().getSequence());
        withdrawal.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        withdrawal.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        withdrawal.setWithdrawalId(event.getSource());
        withdrawal.setWithdrawalTransferStatus(TBaseUtil.unionFieldToEnum(status, WithdrawalTransferStatus.class));

        withdrawalDao.updateNotCurrent(event.getSource());
        long id = withdrawalDao.save(withdrawal);

        List<FistfulCashFlow> cashFlows = fistfulCashFlowDao.getByObjId(withdrawal.getId(), FistfulCashFlowChangeType.withdrawal);
        cashFlows.forEach(pcf -> {
            pcf.setId(null);
            pcf.setObjId(id);
        });
        fistfulCashFlowDao.save(cashFlows);
        log.info("Withdrawal transfer status have been changed, eventId={}, walletId={}, transferChange={}", event.getId(), event.getSource(), change.getTransfer());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }
}
