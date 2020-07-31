package com.rbkmoney.newway.poller.event_stock.impl.withdrawal_session;

import com.rbkmoney.fistful.withdrawal_session.Change;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.WithdrawalSession;
import com.rbkmoney.newway.poller.event_stock.Handler;

public abstract class AbstractWithdrawalSessionHandler implements Handler<Change, MachineEvent> {

    protected void initDefaultFields(MachineEvent event, long sequenceId, WithdrawalSession withdrawalSession,
                                     String withdrawalSessionId) {
        withdrawalSession.setId(null);
        withdrawalSession.setWtime(null);
        withdrawalSession.setSequenceId((int) sequenceId);
        withdrawalSession.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        withdrawalSession.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        withdrawalSession.setWithdrawalSessionId(withdrawalSessionId);

    }

}
