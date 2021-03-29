package com.rbkmoney.newway.handler.event.stock.impl.withdrawal;


import com.rbkmoney.fistful.withdrawal.TimestampedChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.handler.event.stock.Handler;

public interface WithdrawalHandler extends Handler<TimestampedChange, MachineEvent> {
}
