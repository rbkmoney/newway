package com.rbkmoney.newway.poller.event.stock.impl.destination;

import com.rbkmoney.fistful.destination.TimestampedChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.poller.event.stock.Handler;

public interface DestinationHandler extends Handler<TimestampedChange, MachineEvent> {
}
