package com.rbkmoney.newway.poller.event_stock.impl.deposit;

import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Deposit;
import com.rbkmoney.newway.poller.event_stock.Handler;

public abstract class AbstractDepositHandler implements Handler<TimestampedChange, MachineEvent> {

    protected void initDefaultFieldsDeposit(MachineEvent event,
                                            long sequenceId,
                                            String depositId,
                                            Deposit deposit,
                                            String occuredAt) {
        deposit.setId(null);
        deposit.setWtime(null);
        deposit.setSequenceId((int) sequenceId);
        deposit.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        deposit.setEventOccuredAt(TypeUtil.stringToLocalDateTime(occuredAt));
        deposit.setDepositId(depositId);
    }

}
