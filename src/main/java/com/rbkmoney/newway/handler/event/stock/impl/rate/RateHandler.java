package com.rbkmoney.newway.handler.event.stock.impl.rate;

import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.handler.event.stock.Handler;
import com.rbkmoney.xrates.rate.Change;

public interface RateHandler extends Handler<Change, MachineEvent> {
}
