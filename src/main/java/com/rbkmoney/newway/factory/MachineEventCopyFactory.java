package com.rbkmoney.newway.factory;

import com.rbkmoney.machinegun.eventsink.MachineEvent;

public interface MachineEventCopyFactory<T, K> {

    T create(MachineEvent event, long sequenceId, K id, T copiedObject, String occurredAt);

    T create(MachineEvent event, long sequenceId, K id, String occurredAt);

}
