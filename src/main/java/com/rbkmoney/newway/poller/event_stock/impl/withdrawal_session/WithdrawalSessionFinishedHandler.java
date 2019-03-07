package com.rbkmoney.newway.poller.event_stock.impl.withdrawal_session;

import com.rbkmoney.fistful.base.TransactionInfo;
import com.rbkmoney.fistful.withdrawal_session.Change;
import com.rbkmoney.fistful.withdrawal_session.SinkEvent;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.withdrawal_session.iface.WithdrawalSessionDao;
import com.rbkmoney.newway.domain.enums.WithdrawalSessionStatus;
import com.rbkmoney.newway.domain.enums.WithdrawalStatus;
import com.rbkmoney.newway.domain.tables.pojos.WithdrawalSession;
import com.rbkmoney.newway.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WithdrawalSessionFinishedHandler extends AbstractWithdrawalSessionHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WithdrawalSessionDao withdrawalSessionDao;

    private final Filter filter;

    public WithdrawalSessionFinishedHandler(WithdrawalSessionDao withdrawalSessionDao) {
        this.withdrawalSessionDao = withdrawalSessionDao;
        this.filter = new PathConditionFilter(new PathConditionRule("finished", new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(Change change, SinkEvent event) {
        log.info("Start withdrawal session next state handling (eventId={}, sessionId={})",
                event.getId(), event.getSource());
        WithdrawalSession withdrawalSession = withdrawalSessionDao.get(event.getSource());
        withdrawalSession.setId(null);
        withdrawalSession.setWtime(null);
        withdrawalSession.setEventId(event.getId());
        withdrawalSession.setSequenceId(event.getPayload().getSequence());
        withdrawalSession.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        withdrawalSession.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        withdrawalSession.setWithdrawalSessionId(event.getSource());

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
        }

        withdrawalSessionDao.updateNotCurrent(event.getSource());
        long id = withdrawalSessionDao.save(withdrawalSession);

        log.info("Withdrawal session state have been changed (Id={}, eventId={}, sourceId={}, status={})",
                id, event.getId(), event.getSource(), withdrawalSession.getWithdrawalSessionStatus());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }
}
