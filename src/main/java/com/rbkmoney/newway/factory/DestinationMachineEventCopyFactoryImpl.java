package com.rbkmoney.newway.factory;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Destination;
import org.springframework.stereotype.Component;

@Component
public class DestinationMachineEventCopyFactoryImpl implements MachineEventCopyFactory<Destination> {

    @Override
    public Destination create(MachineEvent event, long sequenceId, String id, Destination old, String occurredAt) {
        Destination destination = null;
        if (old != null) {
            destination = new Destination(old);
        } else {
            destination = new Destination();
        }
        destination.setId(null);
        destination.setWtime(null);
        destination.setDestinationId(id);
        destination.setSequenceId((int) sequenceId);
        destination.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        destination.setEventOccuredAt(TypeUtil.stringToLocalDateTime(occurredAt));
        return destination;
    }

    @Override
    public Destination create(MachineEvent event, long sequenceId, String id, String occurredAt) {
        return create(event, sequenceId, id, null, occurredAt);
    }

}
