package com.rbkmoney.newway.factory;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Withdrawal;
import org.springframework.stereotype.Component;

@Component
public class WithdrawalMachineEventCopyFactoryImpl implements MachineEventCopyFactory<Withdrawal, String> {

    @Override
    public Withdrawal create(MachineEvent event, Long sequenceId, String withdrawalId, Withdrawal withdrawalOld,
                             String occurredAt) {
        Withdrawal withdrawal = null;
        if (withdrawalOld != null) {
            withdrawal = new Withdrawal(withdrawalOld);
        } else {
            withdrawal = new Withdrawal();
        }
        withdrawal.setId(null);
        withdrawal.setWtime(null);
        withdrawal.setSequenceId(sequenceId.intValue());
        withdrawal.setWithdrawalId(withdrawalId);
        withdrawal.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        withdrawal.setEventOccuredAt(TypeUtil.stringToLocalDateTime(occurredAt));
        return withdrawal;
    }

    @Override
    public Withdrawal create(MachineEvent event, Long sequenceId, String id, String occurredAt) {
        return create(event, sequenceId, id, null, occurredAt);
    }

}
