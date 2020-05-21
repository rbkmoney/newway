package com.rbkmoney.newway.poller.event_stock.impl.rate;

import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.poller.event_stock.Handler;
import com.rbkmoney.xrates.rate.Change;

public abstract class AbstractRateHandler implements Handler<Change, MachineEvent> {
}
