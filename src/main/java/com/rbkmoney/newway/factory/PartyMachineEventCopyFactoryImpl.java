package com.rbkmoney.newway.factory;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Party;
import org.springframework.stereotype.Component;

@Component
public class PartyMachineEventCopyFactoryImpl implements MachineEventCopyFactory<Party, Integer> {

    @Override
    public Party create(MachineEvent event, Long sequenceId, Integer id, Party old,
                        String occurredAt) {
        Party party = null;
        if (old != null) {
            party = new Party(old);
        } else {
            party = new Party();
        }
        party.setId(null);
        party.setRevision(null);
        party.setWtime(null);
        party.setSequenceId(sequenceId.intValue());
        party.setChangeId(id);
        party.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        return party;
    }

    @Override
    public Party create(MachineEvent event, Long sequenceId, Integer id, String occurredAt) {
        return create(event, sequenceId, id, null, occurredAt);
    }

}
