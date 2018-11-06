package com.rbkmoney.newway.poller.event_stock.impl.withdrawal;

import com.rbkmoney.fistful.withdrawal.SinkEvent;
import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.newway.poller.event_stock.Handler;

public abstract class AbstractWithdrawalHandler implements Handler<Change, SinkEvent> {
}
