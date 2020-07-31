package com.rbkmoney.newway.poller.event_stock.impl.identity;

import com.rbkmoney.fistful.identity.ChallengeChange;
import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Challenge;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import com.rbkmoney.newway.poller.event_stock.Handler;

public abstract class AbstractIdentityHandler implements Handler<Change, MachineEvent> {

    protected void initDefaultFieldsIdentity(Change change, MachineEvent event, long sequenceId, String identityId, Identity identity) {
        identity.setId(null);
        identity.setWtime(null);
        identity.setIdentityId(identityId);
        identity.setSequenceId((int) sequenceId);
        identity.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        identity.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        identity.setIdentityEffectiveChalengeId(change.getEffectiveChallengeChanged());
    }

    protected void initDefaultChallengeFields(MachineEvent event, ChallengeChange challengeChange, int sequenceId, String identityId, Challenge challenge) {
        challenge.setId(null);
        challenge.setWtime(null);
        challenge.setSequenceId(sequenceId);
        challenge.setIdentityId(identityId);
        challenge.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        challenge.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        challenge.setChallengeId(challengeChange.getId());
    }

}
