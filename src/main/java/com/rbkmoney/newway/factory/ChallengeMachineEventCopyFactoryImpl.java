package com.rbkmoney.newway.factory;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Challenge;
import org.springframework.stereotype.Component;

@Component
public class ChallengeMachineEventCopyFactoryImpl implements MachineEventCopyFactory<Challenge, String> {

    @Override
    public Challenge create(MachineEvent event, long sequenceId, String identityId, Challenge old, String occurredAt) {
        Challenge challenge = null;
        if (old != null) {
            challenge = new Challenge(old);
        } else {
            challenge = new Challenge();
        }
        challenge.setId(null);
        challenge.setWtime(null);
        challenge.setSequenceId((int) sequenceId);
        challenge.setIdentityId(identityId);
        challenge.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        challenge.setEventOccuredAt(TypeUtil.stringToLocalDateTime(occurredAt));
        return challenge;
    }

    @Override
    public Challenge create(MachineEvent event, long sequenceId, String id, String occurredAt) {
        return create(event, sequenceId, id, null, occurredAt);
    }

}
