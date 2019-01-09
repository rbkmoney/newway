package com.rbkmoney.newway.poller.event_stock.impl.rate;

import com.rbkmoney.newway.poller.event_stock.Handler;
import com.rbkmoney.xrates.rate.Change;
import com.rbkmoney.xrates.rate.SinkEvent;

public abstract class AbstractRateHandler implements Handler<Change, SinkEvent> {
}
