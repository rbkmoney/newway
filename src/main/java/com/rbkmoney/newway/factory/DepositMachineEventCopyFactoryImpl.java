package com.rbkmoney.newway.factory;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Deposit;
import org.springframework.stereotype.Component;

@Component
public class DepositMachineEventCopyFactoryImpl implements MachineEventCopyFactory<Deposit, String> {

    @Override
    public Deposit create(MachineEvent event, Long sequenceId, String id, Deposit depositOld,
                          String occurredAt) {
        Deposit deposit = null;
        if (depositOld != null) {
            deposit = new Deposit(depositOld);
        } else {
            deposit = new Deposit();
        }
        deposit.setId(null);
        deposit.setWtime(null);
        deposit.setSequenceId(sequenceId.intValue());
        deposit.setDepositId(id);
        deposit.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        deposit.setEventOccuredAt(TypeUtil.stringToLocalDateTime(occurredAt));
        return deposit;
    }

    @Override
    public Deposit create(MachineEvent event, Long sequenceId, String id, String occurredAt) {
        return create(event, sequenceId, id, null, occurredAt);
    }

}
