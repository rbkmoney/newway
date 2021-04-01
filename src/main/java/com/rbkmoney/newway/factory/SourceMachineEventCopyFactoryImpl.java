package com.rbkmoney.newway.factory;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Source;
import org.springframework.stereotype.Component;

@Component
public class SourceMachineEventCopyFactoryImpl implements MachineEventCopyFactory<Source, String> {

    @Override
    public Source create(MachineEvent event, Long sequenceId, String id, Source old, String occurredAt) {
        Source source = null;
        if (old != null) {
            source = new Source(old);
        } else {
            source = new Source();
        }
        source.setId(null);
        source.setWtime(null);
        source.setSequenceId(sequenceId.intValue());
        source.setSourceId(id);
        source.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        source.setEventOccuredAt(TypeUtil.stringToLocalDateTime(occurredAt));
        return source;
    }

    @Override
    public Source create(MachineEvent event, Long sequenceId, String id, String occurredAt) {
        return create(event, sequenceId, id, null, occurredAt);
    }

}
