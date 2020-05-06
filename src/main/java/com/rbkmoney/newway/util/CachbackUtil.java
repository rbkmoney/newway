package com.rbkmoney.newway.util;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Chargeback;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CachbackUtil {

    public static void resetBaseFields(Chargeback chargeback,
                                       MachineEvent machineEvent,
                                       Integer changeId,
                                       Long sequenceId) {
        chargeback.setId(null);
        chargeback.setWtime(null);
        chargeback.setChangeId(changeId);
        chargeback.setSequenceId(sequenceId);
        chargeback.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));
    }

}
