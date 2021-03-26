package com.rbkmoney.newway.poller.event.stock.impl.wallet;

import com.rbkmoney.fistful.wallet.TimestampedChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.poller.event.stock.Handler;

public interface WalletHandler extends Handler<TimestampedChange, MachineEvent> {
}
