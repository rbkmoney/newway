package com.rbkmoney.newway.poller.event_stock.impl.withdrawal;


import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Withdrawal;
import com.rbkmoney.newway.poller.event_stock.Handler;

public abstract class AbstractWithdrawalHandler implements Handler<Change, MachineEvent> {

    protected void initDefaultFields(MachineEvent event, long sequenceId, String withdrawalId, Withdrawal withdrawal) {
        withdrawal.setId(null);
        withdrawal.setWtime(null);
        withdrawal.setSequenceId((int) sequenceId);
        withdrawal.setWithdrawalId(withdrawalId);
        withdrawal.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        withdrawal.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
    }

}
