package com.rbkmoney.newway.poller.event.stock.impl.withdrawal.session;

import com.rbkmoney.fistful.base.TransactionInfo;
import com.rbkmoney.fistful.withdrawal_session.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.withdrawal.session.iface.WithdrawalSessionDao;
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
public class WithdrawalSessionTransactionBoundHandler extends AbstractWithdrawalSessionHandler {

    private final WithdrawalSessionDao withdrawalSessionDao;

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
        WithdrawalSession withdrawalSession = withdrawalSessionDao.get(withdrawalSessionId);
        Long oldId = withdrawalSession.getId();
        initDefaultFields(
                event, sequenceId, withdrawalSession, withdrawalSessionId, timestampedChange.getOccuredAt()
        );
        TransactionBoundChange transactionBound = change.getTransactionBound();
        TransactionInfo trxInfo = transactionBound.getTrxInfo();
        withdrawalSession.setTranInfoId(trxInfo.getId());
        if (trxInfo.isSetTimestamp()) {
            withdrawalSession.setTranInfoTimestamp(TypeUtil.stringToLocalDateTime(trxInfo.getTimestamp()));
        }
        withdrawalSession.setTranInfoJson(JsonUtil.objectToJsonString(trxInfo.getExtra()));
        if (trxInfo.isSetAdditionalInfo()) {
            withdrawalSession.setTranAdditionalInfoRrn(trxInfo.getAdditionalInfo().getRrn());
            withdrawalSession.setTranAdditionalInfoJson(JsonUtil.thriftBaseToJsonString(trxInfo.getAdditionalInfo()));
        }

        withdrawalSessionDao.save(withdrawalSession).ifPresentOrElse(
                id -> {
                    withdrawalSessionDao.updateNotCurrent(oldId);
                    log.info("Withdrawal session transaction bound have been changed, sequenceId={}, " +
                                    "withdrawalSessionId={}, WithdrawalSessionStatus={}", sequenceId,
                            withdrawalSessionId, withdrawalSession.getWithdrawalSessionStatus());
                },
                () -> log.info("Withdrawal session transaction bound have been changed, sequenceId={}, " +
                                "withdrawalSessionId={}, WithdrawalSessionStatus={}", sequenceId,
                        withdrawalSessionId, withdrawalSession.getWithdrawalSessionStatus()));

    }
}
