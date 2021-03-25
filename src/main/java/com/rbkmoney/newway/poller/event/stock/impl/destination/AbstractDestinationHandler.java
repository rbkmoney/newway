package com.rbkmoney.newway.poller.event.stock.impl.destination;

import com.rbkmoney.fistful.destination.TimestampedChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Destination;
import com.rbkmoney.newway.poller.event.stock.Handler;

public abstract class AbstractDestinationHandler implements Handler<TimestampedChange, MachineEvent> {

    protected void initDefaultFields(MachineEvent event, long sequenceId, String destinationId, Destination destination,
                                     String occuredAt) {
        destination.setId(null);
        destination.setWtime(null);
        destination.setDestinationId(destinationId);
        destination.setSequenceId((int) sequenceId);
        destination.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        destination.setEventOccuredAt(TypeUtil.stringToLocalDateTime(occuredAt));
    }

}
