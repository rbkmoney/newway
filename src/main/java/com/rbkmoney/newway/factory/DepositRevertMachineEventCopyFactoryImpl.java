package com.rbkmoney.newway.factory;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.DepositRevert;
import org.springframework.stereotype.Component;

@Component
public class DepositRevertMachineEventCopyFactoryImpl implements MachineEventCopyFactory<DepositRevert, String> {

    @Override
    public DepositRevert create(MachineEvent event, long sequenceId, String id, DepositRevert old, String occurredAt) {
        DepositRevert depositRevert = null;
        if (old != null) {
            depositRevert = new DepositRevert(old);
        } else {
            depositRevert = new DepositRevert();
        }
        depositRevert.setId(null);
        depositRevert.setWtime(null);
        depositRevert.setSequenceId((int) sequenceId);
        depositRevert.setDepositId(id);
        depositRevert.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        depositRevert.setEventOccuredAt(TypeUtil.stringToLocalDateTime(occurredAt));
        return depositRevert;
    }

    @Override
    public DepositRevert create(MachineEvent event, long sequenceId, String id, String occurredAt) {
        return create(event, sequenceId, id, null, occurredAt);
    }

}
