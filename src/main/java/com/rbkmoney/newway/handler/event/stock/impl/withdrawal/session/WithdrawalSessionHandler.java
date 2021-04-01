package com.rbkmoney.newway.handler.event.stock.impl.withdrawal.session;

import com.rbkmoney.fistful.withdrawal_session.TimestampedChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.handler.event.stock.Handler;

public interface WithdrawalSessionHandler extends Handler<TimestampedChange, MachineEvent> {
}
