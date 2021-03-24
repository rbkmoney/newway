package com.rbkmoney.newway.poller.event_stock.impl.deposit;

import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.*;
import com.rbkmoney.newway.poller.event_stock.Handler;

public abstract class AbstractDepositHandler implements Handler<TimestampedChange, MachineEvent> {

    protected void initDefaultFieldsDeposit(MachineEvent event,
                                            long sequenceId,
                                            Deposit deposit,
                                            String occuredAt) {
        deposit.setId(null);
        deposit.setWtime(null);
        deposit.setSequenceId((int) sequenceId);
        deposit.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        deposit.setEventOccuredAt(TypeUtil.stringToLocalDateTime(occuredAt));
    }

    protected void initDefaultFieldsRevert(String createdAt,
                                           String occuredAt,
                                           long sequenceId,
                                           DepositRevert depositRevert) {
        depositRevert.setId(null);
        depositRevert.setWtime(null);
        depositRevert.setSequenceId((int) sequenceId);
        depositRevert.setEventCreatedAt(TypeUtil.stringToLocalDateTime(createdAt));
        depositRevert.setEventOccuredAt(TypeUtil.stringToLocalDateTime(occuredAt));
    }

    protected void initDefaultFieldsAdjustment(String createdAt,
                                               String occuredAt,
                                               long sequenceId,
                                               DepositAdjustment depositAdjustment) {
        depositAdjustment.setId(null);
        depositAdjustment.setWtime(null);
        depositAdjustment.setSequenceId((int) sequenceId);
        depositAdjustment.setEventCreatedAt(TypeUtil.stringToLocalDateTime(createdAt));
        depositAdjustment.setEventOccuredAt(TypeUtil.stringToLocalDateTime(occuredAt));
    }
}
