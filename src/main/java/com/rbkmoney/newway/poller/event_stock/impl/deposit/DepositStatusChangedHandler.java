package com.rbkmoney.newway.poller.event_stock.impl.deposit;

import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.SinkEvent;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.deposit.iface.DepositDao;
import com.rbkmoney.newway.dao.withdrawal.iface.FistfulCashFlowDao;
import com.rbkmoney.newway.domain.enums.DepositStatus;
import com.rbkmoney.newway.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.newway.domain.tables.pojos.Deposit;
import com.rbkmoney.newway.domain.tables.pojos.FistfulCashFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DepositStatusChangedHandler extends AbstractDepositHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final DepositDao depositDao;

    private final FistfulCashFlowDao fistfulCashFlowDao;

    private final Filter filter;

    public DepositStatusChangedHandler(DepositDao depositDao, FistfulCashFlowDao fistfulCashFlowDao) {
        this.depositDao = depositDao;
        this.fistfulCashFlowDao = fistfulCashFlowDao;
        this.filter = new PathConditionFilter(new PathConditionRule("status_changed", new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(Change change, SinkEvent event) {
        log.info("Start deposit status changed handling, eventId={}, depositId={}, status={}", event.getId(), event.getSource(), change.getStatusChanged());
        Deposit deposit = depositDao.get(event.getSource());

        long sourceId = deposit.getId();
        deposit.setId(null);
        deposit.setWtime(null);
        deposit.setEventId(event.getId());
        deposit.setSequenceId(event.getPayload().getSequence());
        deposit.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        deposit.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        deposit.setDepositId(event.getSource());
        deposit.setDepositStatus(TBaseUtil.unionFieldToEnum(change.getStatusChanged(), DepositStatus.class));

        depositDao.updateNotCurrent(event.getSource());
        long id = depositDao.save(deposit);

        List<FistfulCashFlow> cashFlows = fistfulCashFlowDao.getByObjId(sourceId, FistfulCashFlowChangeType.deposit);
        cashFlows.forEach(pcf -> {
            pcf.setId(null);
            pcf.setObjId(id);
        });
        fistfulCashFlowDao.save(cashFlows);
        log.info("Deposit status have been changed, eventId={}, walletId={}, status={}", event.getId(), event.getSource(), change.getStatusChanged());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }
}
