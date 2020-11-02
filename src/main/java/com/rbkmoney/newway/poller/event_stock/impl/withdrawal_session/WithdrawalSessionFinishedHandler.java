package com.rbkmoney.newway.poller.event_stock.impl.withdrawal_session;

import com.rbkmoney.fistful.withdrawal_session.Change;
import com.rbkmoney.fistful.withdrawal_session.TimestampedChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.withdrawal_session.iface.WithdrawalSessionDao;
import com.rbkmoney.newway.domain.enums.WithdrawalSessionStatus;
import com.rbkmoney.newway.domain.tables.pojos.WithdrawalSession;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalSessionFinishedHandler extends AbstractWithdrawalSessionHandler {

    private final WithdrawalSessionDao withdrawalSessionDao;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.finished", new IsNullCondition().not())
    );

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        long sequenceId = event.getEventId();
        String withdrawalSessionId = event.getSourceId();
        log.info("Start withdrawal session next state handling, sequenceId={}, withdrawalSessionId={}",
                sequenceId, withdrawalSessionId);
        WithdrawalSession withdrawalSession = withdrawalSessionDao.get(withdrawalSessionId);
        Long oldId = withdrawalSession.getId();
        initDefaultFields(event, sequenceId, withdrawalSession, withdrawalSessionId, timestampedChange.getOccuredAt());

        withdrawalSession.setWithdrawalSessionStatus(TBaseUtil.unionFieldToEnum(change.getFinished(), WithdrawalSessionStatus.class));
        if (change.getFinished().isSetFailed()) {
            withdrawalSession.setFailureJson(JsonUtil.tBaseToJsonString(change.getFinished().getFailed()));
        }
        withdrawalSessionDao.save(withdrawalSession).ifPresentOrElse(
                id -> {
                    withdrawalSessionDao.updateNotCurrent(oldId);
                    log.info("Withdrawal session state have been changed, sequenceId={}, withdrawalSessionId={}, WithdrawalSessionStatus={}",
                            sequenceId, withdrawalSessionId, withdrawalSession.getWithdrawalSessionStatus());
                },
                () -> log.info("Withdrawal session state have been changed, sequenceId={}, withdrawalSessionId={}, WithdrawalSessionStatus={}",
                        sequenceId, withdrawalSessionId, withdrawalSession.getWithdrawalSessionStatus()));

    }

}
