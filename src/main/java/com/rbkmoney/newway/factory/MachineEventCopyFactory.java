package com.rbkmoney.newway.factory;

import com.rbkmoney.machinegun.eventsink.MachineEvent;

public interface MachineEventCopyFactory<T> {

    T create(MachineEvent event, long sequenceId, String id, T copiedObject, String occurredAt);

    T create(MachineEvent event, long sequenceId, String id, String occurredAt);

}
