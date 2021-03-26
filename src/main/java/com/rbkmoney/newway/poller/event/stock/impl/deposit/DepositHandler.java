package com.rbkmoney.newway.poller.event.stock.impl.deposit;

import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.poller.event.stock.Handler;

public interface DepositHandler extends Handler<TimestampedChange, MachineEvent> {
}
