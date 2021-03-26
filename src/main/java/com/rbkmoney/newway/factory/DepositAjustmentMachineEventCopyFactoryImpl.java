package com.rbkmoney.newway.factory;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.DepositAdjustment;
import org.springframework.stereotype.Component;

@Component
public class DepositAjustmentMachineEventCopyFactoryImpl implements MachineEventCopyFactory<DepositAdjustment> {

    @Override
    public DepositAdjustment create(MachineEvent event, long sequenceId, String id, DepositAdjustment old,
                                    String occurredAt) {
        DepositAdjustment depositAdjustment = null;
        if (old != null) {
            depositAdjustment = new DepositAdjustment(old);
        } else {
            depositAdjustment = new DepositAdjustment();
        }
        depositAdjustment.setId(null);
        depositAdjustment.setWtime(null);
        depositAdjustment.setSequenceId((int) sequenceId);
        depositAdjustment.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        depositAdjustment.setEventOccuredAt(TypeUtil.stringToLocalDateTime(occurredAt));
        return depositAdjustment;
    }

    @Override
    public DepositAdjustment create(MachineEvent event, long sequenceId, String id, String occurredAt) {
        return create(event, sequenceId, id, null, occurredAt);
    }

}
