package com.rbkmoney.newway.factory;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Adjustment;
import org.springframework.stereotype.Component;

@Component
public class AdjustmentMachineEventCopyFactoryImpl implements MachineEventCopyFactory<Adjustment, Integer> {

    @Override
    public Adjustment create(MachineEvent event, Long sequenceId, Integer id, Adjustment old, String occurredAt) {
        Adjustment chargeback = null;
        if (old != null) {
            chargeback = new Adjustment(old);
        } else {
            chargeback = new Adjustment();
        }
        chargeback.setId(null);
        chargeback.setWtime(null);
        chargeback.setChangeId(id);
        chargeback.setSequenceId(sequenceId);
        chargeback.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        return chargeback;
    }

    @Override
    public Adjustment create(MachineEvent event, Long sequenceId, Integer id, String occurredAt) {
        return create(event, sequenceId, id, null, occurredAt);
    }

}
