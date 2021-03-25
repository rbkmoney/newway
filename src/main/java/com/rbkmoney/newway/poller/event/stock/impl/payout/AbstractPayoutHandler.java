package com.rbkmoney.newway.poller.event.stock.impl.payout;

import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.newway.poller.event.stock.Handler;

public abstract class AbstractPayoutHandler implements Handler<PayoutChange, Event> {
}
