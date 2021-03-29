package com.rbkmoney.newway.factory;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Chargeback;
import org.springframework.stereotype.Component;

@Component
public class ChargebackMachineEventCopyFactoryImpl implements MachineEventCopyFactory<Chargeback, Integer> {

    @Override
    public Chargeback create(MachineEvent event, Long sequenceId, Integer id, Chargeback old, String occurredAt) {
        Chargeback chargeback = null;
        if (old != null) {
            chargeback = new Chargeback(old);
        } else {
            chargeback = new Chargeback();
        }
        chargeback.setId(null);
        chargeback.setWtime(null);
        chargeback.setChangeId(id);
        chargeback.setSequenceId(sequenceId);
        chargeback.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        return chargeback;
    }

    @Override
    public Chargeback create(MachineEvent event, Long sequenceId, Integer id, String occurredAt) {
        return create(event, sequenceId, id, null, occurredAt);
    }

}
