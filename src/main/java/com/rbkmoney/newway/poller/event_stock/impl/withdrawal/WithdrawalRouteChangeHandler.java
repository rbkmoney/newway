package com.rbkmoney.newway.poller.event_stock.impl.withdrawal;

import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.SinkEvent;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.withdrawal.iface.FistfulCashFlowDao;
import com.rbkmoney.newway.dao.withdrawal.iface.WithdrawalDao;
import com.rbkmoney.newway.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Withdrawal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class WithdrawalRouteChangeHandler extends AbstractWithdrawalHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WithdrawalDao withdrawalDao;

    private final FistfulCashFlowDao fistfulCashFlowDao;

    private final Filter filter;

    public WithdrawalRouteChangeHandler(WithdrawalDao withdrawalDao, FistfulCashFlowDao fistfulCashFlowDao) {
        this.withdrawalDao = withdrawalDao;
        this.fistfulCashFlowDao = fistfulCashFlowDao;
        this.filter = new PathConditionFilter(new PathConditionRule("route", new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(Change change, SinkEvent event) {
        log.info("Start withdrawal provider id changed handling, eventId={}, walletId={}, providerId={}", event.getId(), event.getSource(), change.getRoute().getId());
        Withdrawal withdrawal = withdrawalDao.get(event.getSource());

        long sourceId = withdrawal.getId();
        withdrawal.setId(null);
        withdrawal.setWtime(null);
        withdrawal.setEventId(event.getId());
        withdrawal.setSequenceId(event.getPayload().getSequence());
        withdrawal.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        withdrawal.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        withdrawal.setWithdrawalId(event.getSource());
        withdrawal.setProviderId(change.getRoute().getId());

        withdrawalDao.updateNotCurrent(event.getSource());
        long id = withdrawalDao.save(withdrawal);

        List<FistfulCashFlow> cashFlows = fistfulCashFlowDao.getByObjId(sourceId);
        cashFlows.forEach(pcf -> {
            pcf.setId(null);
            pcf.setObjId(id);
        });
        fistfulCashFlowDao.save(cashFlows);
        log.info("Withdrawal provider id have been changed, eventId={}, walletId={}, providerId={}", event.getId(), event.getSource(), change.getRoute().getId());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }
}
