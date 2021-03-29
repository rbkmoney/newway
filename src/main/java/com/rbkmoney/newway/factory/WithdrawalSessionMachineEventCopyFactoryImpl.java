package com.rbkmoney.newway.factory;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.WithdrawalSession;
import org.springframework.stereotype.Component;

@Component
public class WithdrawalSessionMachineEventCopyFactoryImpl
        implements MachineEventCopyFactory<WithdrawalSession, String> {

    @Override
    public WithdrawalSession create(MachineEvent event, long sequenceId,
                                    String withdrawalSessionId,
                                    WithdrawalSession withdrawalSessionOld,
                                    String occurredAt) {
        WithdrawalSession withdrawalSession = null;
        if (withdrawalSessionOld != null) {
            withdrawalSession = new WithdrawalSession(withdrawalSessionOld);
        } else {
            withdrawalSession = new WithdrawalSession();
        }
        withdrawalSession.setId(null);
        withdrawalSession.setWtime(null);
        withdrawalSession.setSequenceId((int) sequenceId);
        withdrawalSession.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        withdrawalSession.setEventOccuredAt(TypeUtil.stringToLocalDateTime(occurredAt));
        withdrawalSession.setWithdrawalSessionId(withdrawalSessionId);
        return withdrawalSession;
    }

    @Override
    public WithdrawalSession create(MachineEvent event, long sequenceId, String id, String occurredAt) {
        return create(event, sequenceId, id, null, occurredAt);
    }

}
