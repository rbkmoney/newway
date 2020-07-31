package com.rbkmoney.newway.poller.event_stock.impl.wallet;

import com.rbkmoney.fistful.wallet.Change;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Wallet;
import com.rbkmoney.newway.poller.event_stock.Handler;

public abstract class AbstractWalletHandler implements Handler<Change, MachineEvent> {

    protected void initDefaultFields(MachineEvent event, long sequenceId, String walletId, Wallet wallet) {
        wallet.setId(null);
        wallet.setWtime(null);
        wallet.setSequenceId((int) sequenceId);
        wallet.setWalletId(walletId);
        wallet.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        wallet.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
    }

}
