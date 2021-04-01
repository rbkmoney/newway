package com.rbkmoney.newway.handler.event.stock.impl.withdrawal.session;

import com.rbkmoney.fistful.base.TransactionInfo;
import com.rbkmoney.fistful.withdrawal_session.Change;
import com.rbkmoney.fistful.withdrawal_session.TimestampedChange;
import com.rbkmoney.fistful.withdrawal_session.TransactionBoundChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.withdrawal.session.iface.WithdrawalSessionDao;
import com.rbkmoney.newway.domain.tables.pojos.WithdrawalSession;
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
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
public class WithdrawalSessionTransactionBoundHandler implements WithdrawalSessionHandler {

    private final WithdrawalSessionDao withdrawalSessionDao;
    private final MachineEventCopyFactory<WithdrawalSession, String> withdrawalSessionMachineEventCopyFactory;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.transaction_bound", new IsNullCondition().not())
    );

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        long sequenceId = event.getEventId();
        String withdrawalSessionId = event.getSourceId();
        log.info("Start withdrawal transaction bound handling, sequenceId={}, withdrawalSessionId={}",
                sequenceId, withdrawalSessionId);

        final WithdrawalSession withdrawalSessionOld = withdrawalSessionDao.get(withdrawalSessionId);
        WithdrawalSession withdrawalSessionNew = withdrawalSessionMachineEventCopyFactory
                .create(event, sequenceId, withdrawalSessionId, withdrawalSessionOld, timestampedChange.getOccuredAt());

        TransactionBoundChange transactionBound = change.getTransactionBound();
        TransactionInfo trxInfo = transactionBound.getTrxInfo();
        withdrawalSessionNew.setTranInfoId(trxInfo.getId());
        if (trxInfo.isSetTimestamp()) {
            withdrawalSessionNew.setTranInfoTimestamp(TypeUtil.stringToLocalDateTime(trxInfo.getTimestamp()));
        }
        withdrawalSessionNew.setTranInfoJson(JsonUtil.objectToJsonString(trxInfo.getExtra()));
        if (trxInfo.isSetAdditionalInfo()) {
            withdrawalSessionNew.setTranAdditionalInfoRrn(trxInfo.getAdditionalInfo().getRrn());
            withdrawalSessionNew
                    .setTranAdditionalInfoJson(JsonUtil.thriftBaseToJsonString(trxInfo.getAdditionalInfo()));
        }

        withdrawalSessionDao.save(withdrawalSessionNew).ifPresentOrElse(
                id -> {
                    withdrawalSessionDao.updateNotCurrent(withdrawalSessionOld.getId());
                    log.info("Withdrawal session transaction bound have been changed, sequenceId={}, " +
                                    "withdrawalSessionId={}, WithdrawalSessionStatus={}", sequenceId,
                            withdrawalSessionId, withdrawalSessionOld.getWithdrawalSessionStatus());
                },
                () -> log.info("Withdrawal session transaction bound have been changed, sequenceId={}, " +
                                "withdrawalSessionId={}, WithdrawalSessionStatus={}", sequenceId,
                        withdrawalSessionId, withdrawalSessionOld.getWithdrawalSessionStatus()));

    }
}
