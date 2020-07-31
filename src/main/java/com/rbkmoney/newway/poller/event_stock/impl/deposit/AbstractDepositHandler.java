package com.rbkmoney.newway.poller.event_stock.impl.deposit;

import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Deposit;
import com.rbkmoney.newway.poller.event_stock.Handler;

public abstract class AbstractDepositHandler implements Handler<Change, MachineEvent> {

    protected void initDefaultFieldsDeposit(MachineEvent event, Integer changeId, long sequenceId, String depositId, Deposit deposit) {
        deposit.setId(null);
        deposit.setWtime(null);
        deposit.setSequenceId((int) sequenceId);
        deposit.setChangeId(changeId);
        deposit.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        deposit.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        deposit.setDepositId(depositId);
    }

}
