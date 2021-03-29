package com.rbkmoney.newway.factory;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletMachineEventCopyFactoryImpl implements MachineEventCopyFactory<Wallet, String> {

    @Override
    public Wallet create(MachineEvent event, long sequenceId, String id, Wallet withdrawalOld,
                         String occurredAt) {
        Wallet wallet = null;
        if (withdrawalOld != null) {
            wallet = new Wallet(withdrawalOld);
        } else {
            wallet = new Wallet();
        }
        wallet.setId(null);
        wallet.setWtime(null);
        wallet.setSequenceId((int) sequenceId);
        wallet.setWalletId(id);
        wallet.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        wallet.setEventOccuredAt(TypeUtil.stringToLocalDateTime(occurredAt));
        return wallet;
    }

    @Override
    public Wallet create(MachineEvent event, long sequenceId, String id, String occurredAt) {
        return create(event, sequenceId, id, null, occurredAt);
    }

}
