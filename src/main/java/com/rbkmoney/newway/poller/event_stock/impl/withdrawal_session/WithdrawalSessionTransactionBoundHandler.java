package com.rbkmoney.newway.poller.event_stock.impl.withdrawal_session;

import com.rbkmoney.fistful.base.TransactionInfo;
import com.rbkmoney.fistful.withdrawal_session.Change;
import com.rbkmoney.fistful.withdrawal_session.SinkEvent;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.withdrawal_session.iface.WithdrawalSessionDao;
import com.rbkmoney.newway.domain.tables.pojos.WithdrawalSession;
import com.rbkmoney.newway.util.JsonUtil;
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

    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("transaction_bound", new IsNullCondition().not())
    );

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(Change change, SinkEvent event) {
        log.info("Start withdrawal session transaction bound handling (eventId={}, sessionId={})",
                event.getId(), event.getSource());
        WithdrawalSession withdrawalSession = withdrawalSessionDao.get(event.getSource());
        withdrawalSession.setId(null);
        withdrawalSession.setWtime(null);
        withdrawalSession.setEventId(event.getId());
        withdrawalSession.setSequenceId(event.getPayload().getSequence());
        withdrawalSession.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        withdrawalSession.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        withdrawalSession.setWithdrawalSessionId(event.getSource());

        TransactionInfo trxInfo = change.getTransactionBound().getTrxInfo();
        withdrawalSession.setTranInfoId(trxInfo.getId());
        if (trxInfo.isSetTimestamp()) {
            withdrawalSession.setTranInfoTimestamp(TypeUtil.stringToLocalDateTime(trxInfo.getTimestamp()));
        }
        withdrawalSession.setTranInfoJson(JsonUtil.objectToJsonString(trxInfo.getExtra()));
        if (trxInfo.isSetAdditionalInfo()) {
            withdrawalSession.setTranAdditionalInfoRrn(trxInfo.getAdditionalInfo().getRrn());
            withdrawalSession.setTranAdditionalInfoJson(JsonUtil.tBaseToJsonString(trxInfo.getAdditionalInfo()));
        }
        withdrawalSessionDao.updateNotCurrent(event.getSource());
        long id = withdrawalSessionDao.save(withdrawalSession);

        log.info("Withdrawal session transaction bound have been changed (Id={}, eventId={}, " +
                        "sourceId={}, status={})", id, event.getId(), event.getSource(),
                withdrawalSession.getWithdrawalSessionStatus());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }
}
