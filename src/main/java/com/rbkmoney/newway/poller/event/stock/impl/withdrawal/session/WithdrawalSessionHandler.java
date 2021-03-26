package com.rbkmoney.newway.poller.event.stock.impl.withdrawal.session;

import com.rbkmoney.fistful.withdrawal_session.TimestampedChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.poller.event.stock.Handler;

public interface WithdrawalSessionHandler extends Handler<TimestampedChange, MachineEvent> {
}
