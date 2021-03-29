package com.rbkmoney.newway.handler.event.stock.impl.payout;

import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.newway.handler.event.stock.Handler;

public interface PayoutHandler extends Handler<PayoutChange, Event> {
}
