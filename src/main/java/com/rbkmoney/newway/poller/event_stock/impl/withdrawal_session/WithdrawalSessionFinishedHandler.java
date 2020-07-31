package com.rbkmoney.newway.poller.event_stock.impl.withdrawal_session;

import com.rbkmoney.fistful.base.TransactionInfo;
import com.rbkmoney.fistful.withdrawal_session.Change;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
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
    private final Filter filter = new PathConditionFilter(new PathConditionRule("finished", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(Change change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        String withdrawalSessionId = event.getSourceId();
        log.info("Start withdrawal session next state handling, sequenceId={}, changeId={}, withdrawalSessionId={}",
                sequenceId, changeId, withdrawalSessionId);
        WithdrawalSession withdrawalSession = withdrawalSessionDao.get(withdrawalSessionId);
        Long oldId = withdrawalSession.getId();
        initDefaultFields(event, sequenceId, withdrawalSession, withdrawalSessionId);

        withdrawalSession.setWithdrawalSessionStatus(TBaseUtil.unionFieldToEnum(change.getFinished(), WithdrawalSessionStatus.class));
        if (change.getFinished().isSetFailed()) {
            withdrawalSession.setFailureJson(JsonUtil.tBaseToJsonString(change.getFinished().getFailed()));
        } else if (change.getFinished().isSetSuccess()) {
            TransactionInfo trxInfo = change.getFinished().getSuccess().getTrxInfo();
            withdrawalSession.setTranInfoId(trxInfo.getId());
            if (trxInfo.isSetTimestamp()) {
                withdrawalSession.setTranInfoTimestamp(TypeUtil.stringToLocalDateTime(trxInfo.getTimestamp()));
            }
            withdrawalSession.setTranInfoJson(JsonUtil.objectToJsonString(trxInfo.getExtra()));
            if (trxInfo.isSetAdditionalInfo()) {
                withdrawalSession.setTranAdditionalInfoRrn(trxInfo.getAdditionalInfo().getRrn());
                withdrawalSession.setTranAdditionalInfoJson(JsonUtil.tBaseToJsonString(trxInfo.getAdditionalInfo()));
            }
        }

        withdrawalSessionDao.save(withdrawalSession).ifPresentOrElse(
                id -> {
                    withdrawalSessionDao.updateNotCurrent(oldId);
                    log.info("Withdrawal session state have been changed, sequenceId={}, changeId={}, withdrawalSessionId={}, WithdrawalSessionStatus={}",
                            sequenceId, changeId, withdrawalSessionId, withdrawalSession.getWithdrawalSessionStatus());
                },
                () -> log.info("Withdrawal session state have been changed, sequenceId={}, changeId={}, withdrawalSessionId={}, WithdrawalSessionStatus={}",
                        sequenceId, changeId, withdrawalSessionId, withdrawalSession.getWithdrawalSessionStatus()));

    }

}
