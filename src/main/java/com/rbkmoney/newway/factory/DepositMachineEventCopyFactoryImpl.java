package com.rbkmoney.newway.factory;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Deposit;
import org.springframework.stereotype.Component;

@Component
public class DepositMachineEventCopyFactoryImpl implements MachineEventCopyFactory<Deposit> {

    @Override
    public Deposit create(MachineEvent event, long sequenceId, String id, Deposit depositOld,
                          String occurredAt) {
        Deposit deposit = null;
        if (depositOld != null) {
            deposit = new Deposit(depositOld);
        } else {
            deposit = new Deposit();
        }
        deposit.setId(null);
        deposit.setWtime(null);
        deposit.setSequenceId((int) sequenceId);
        deposit.setDepositId(id);
        deposit.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        deposit.setEventOccuredAt(TypeUtil.stringToLocalDateTime(occurredAt));
        return deposit;
    }

    @Override
    public Deposit create(MachineEvent event, long sequenceId, String id, String occurredAt) {
        return create(event, sequenceId, id, null, occurredAt);
    }

}
