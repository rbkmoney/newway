package com.rbkmoney.newway.handler.event.stock.impl.source;

import com.rbkmoney.fistful.source.TimestampedChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.handler.event.stock.Handler;

public interface SourceHandler extends Handler<TimestampedChange, MachineEvent> {

}
