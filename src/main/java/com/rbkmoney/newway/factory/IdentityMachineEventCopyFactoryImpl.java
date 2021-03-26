package com.rbkmoney.newway.factory;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.DepositAdjustment;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import org.springframework.stereotype.Component;

@Component
public class IdentityMachineEventCopyFactoryImpl implements MachineEventCopyFactory<Identity> {

    @Override
    public Identity create(MachineEvent event, long sequenceId, String id, Identity old, String occurredAt) {
        Identity identity = null;
        if (old != null) {
            identity = new Identity(old);
        } else {
            identity = new Identity();
        }
        identity.setId(null);
        identity.setWtime(null);
        identity.setIdentityId(id);
        identity.setSequenceId((int) sequenceId);
        identity.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        identity.setEventOccuredAt(TypeUtil.stringToLocalDateTime(occurredAt));
        return identity;
    }

    @Override
    public Identity create(MachineEvent event, long sequenceId, String id, String occurredAt) {
        return create(event, sequenceId, id, null, occurredAt);
    }

}
