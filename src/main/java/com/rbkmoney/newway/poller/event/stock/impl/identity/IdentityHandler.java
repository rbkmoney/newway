package com.rbkmoney.newway.poller.event.stock.impl.identity;

import com.rbkmoney.fistful.identity.TimestampedChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.poller.event.stock.Handler;

public interface IdentityHandler extends Handler<TimestampedChange, MachineEvent> {
}
