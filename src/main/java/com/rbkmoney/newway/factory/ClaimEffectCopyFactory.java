package com.rbkmoney.newway.factory;

import com.rbkmoney.machinegun.eventsink.MachineEvent;

public interface ClaimEffectCopyFactory<T, K> {

    T create(MachineEvent event, long sequenceId, Integer claimEffectId, K id, T copiedObject);

    default T create(MachineEvent event, long sequenceId, Integer claimEffectId, K id) {
        return create(event, sequenceId, claimEffectId, id, null);
    }

}
