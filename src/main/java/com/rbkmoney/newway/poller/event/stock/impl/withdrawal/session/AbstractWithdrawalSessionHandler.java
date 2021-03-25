package com.rbkmoney.newway.poller.event.stock.impl.withdrawal.session;

import com.rbkmoney.fistful.withdrawal_session.TimestampedChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.WithdrawalSession;
import com.rbkmoney.newway.poller.event.stock.Handler;

public abstract class AbstractWithdrawalSessionHandler implements Handler<TimestampedChange, MachineEvent> {

    protected void initDefaultFields(MachineEvent event, long sequenceId, WithdrawalSession withdrawalSession,
                                     String withdrawalSessionId, String occuredAt) {
        withdrawalSession.setId(null);
        withdrawalSession.setWtime(null);
        withdrawalSession.setSequenceId((int) sequenceId);
        withdrawalSession.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        withdrawalSession.setEventOccuredAt(TypeUtil.stringToLocalDateTime(occuredAt));
        withdrawalSession.setWithdrawalSessionId(withdrawalSessionId);
    }

}
