package com.rbkmoney.newway.handler.event.stock.impl.wallet;

import com.rbkmoney.fistful.wallet.TimestampedChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.handler.event.stock.Handler;

public interface WalletHandler extends Handler<TimestampedChange, MachineEvent> {
}
