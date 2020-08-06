package com.rbkmoney.newway.poller.event_stock.impl.withdrawal;

import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.Route;
import com.rbkmoney.fistful.withdrawal.TimestampedChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.withdrawal.iface.FistfulCashFlowDao;
import com.rbkmoney.newway.dao.withdrawal.iface.WithdrawalDao;
import com.rbkmoney.newway.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.newway.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Withdrawal;
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
public class WithdrawalRouteChangeHandler extends AbstractWithdrawalHandler {

    private final WithdrawalDao withdrawalDao;
    private final FistfulCashFlowDao fistfulCashFlowDao;

    @Getter
    private final Filter filter = new PathConditionFilter(new PathConditionRule("route.route", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        Route route = change.getRoute().getRoute();
        int providerId = route.getProviderId();
        String providerIdLegacy = route.getProviderIdLegacy();
        long sequenceId = event.getEventId();
        String withdrawalId = event.getSourceId();
        log.info("Start withdrawal provider id changed handling, sequenceId={}, withdrawalId={}", sequenceId, withdrawalId);

        Withdrawal withdrawal = withdrawalDao.get(withdrawalId);
        Long oldId = withdrawal.getId();

        initDefaultFields(event, sequenceId, withdrawalId, withdrawal, timestampedChange.getOccuredAt());

        withdrawal.setProviderId(providerId);
        withdrawal.setProviderIdLegacy(providerIdLegacy);

        withdrawalDao.save(withdrawal).ifPresentOrElse(
                id -> {
                    withdrawalDao.updateNotCurrent(oldId);
                    List<FistfulCashFlow> cashFlows = fistfulCashFlowDao.getByObjId(withdrawal.getId(), FistfulCashFlowChangeType.withdrawal);
                    cashFlows.forEach(pcf -> {
                        pcf.setId(null);
                        pcf.setObjId(id);
                    });
                    fistfulCashFlowDao.save(cashFlows);
                    log.info("Withdrawal provider id have been changed, sequenceId={}, withdrawalId={}", sequenceId, withdrawalId);
                },
                () -> log.info("Withdrawal provider id have been changed, sequenceId={}, withdrawalId={}", sequenceId, withdrawalId));
    }

}
