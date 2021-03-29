package com.rbkmoney.newway.poller.event.stock.impl.rate;

import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.poller.event.stock.Handler;
import com.rbkmoney.xrates.rate.Change;

public interface RateHandler extends Handler<Change, MachineEvent> {
}
