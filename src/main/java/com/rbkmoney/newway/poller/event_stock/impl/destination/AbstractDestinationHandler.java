package com.rbkmoney.newway.poller.event_stock.impl.destination;

import com.rbkmoney.fistful.destination.Change;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Destination;
import com.rbkmoney.newway.poller.event_stock.Handler;

public abstract class AbstractDestinationHandler implements Handler<Change, MachineEvent> {

    protected void initDefaultFields(MachineEvent event, long sequenceId, String destinationId, Destination destination) {
        destination.setId(null);
        destination.setWtime(null);
        destination.setDestinationId(destinationId);
        destination.setSequenceId((int) sequenceId);
        destination.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        destination.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
    }

}
