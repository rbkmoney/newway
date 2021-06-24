package com.rbkmoney.newway.handler.event.stock.impl.payout;

import com.rbkmoney.newway.handler.event.stock.Handler;
import com.rbkmoney.payout.manager.Event;
import com.rbkmoney.payout.manager.PayoutChange;

public interface PayoutHandler extends Handler<PayoutChange, Event> {
}
