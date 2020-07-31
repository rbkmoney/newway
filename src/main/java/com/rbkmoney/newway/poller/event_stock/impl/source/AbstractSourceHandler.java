package com.rbkmoney.newway.poller.event_stock.impl.source;

import com.rbkmoney.fistful.source.Change;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Source;
import com.rbkmoney.newway.poller.event_stock.Handler;

public abstract class AbstractSourceHandler implements Handler<Change, MachineEvent> {

    protected void initDefaultFields(MachineEvent event, int sequenceId, String sourceId, Source source) {
        source.setId(null);
        source.setWtime(null);
        source.setSequenceId(sequenceId);
        source.setSourceId(sourceId);
        source.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        source.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
    }

}
