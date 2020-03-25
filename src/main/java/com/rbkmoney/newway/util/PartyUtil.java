package com.rbkmoney.newway.util;

import com.rbkmoney.damsel.domain.ShopAccount;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Party;
import com.rbkmoney.newway.domain.tables.pojos.Shop;

public class PartyUtil {

    public static void resetBaseFields(MachineEvent event, Integer changeId, long sequenceId, Party partySource) {
        partySource.setId(null);
        partySource.setRevision(null);
        partySource.setWtime(null);
        partySource.setSequenceId(sequenceId);
        partySource.setChangeId(changeId);
        partySource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
    }
}
