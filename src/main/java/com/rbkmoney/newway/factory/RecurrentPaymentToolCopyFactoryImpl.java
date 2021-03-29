package com.rbkmoney.newway.factory;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.RecurrentPaymentTool;
import org.springframework.stereotype.Component;

@Component
public class RecurrentPaymentToolCopyFactoryImpl implements MachineEventCopyFactory<RecurrentPaymentTool, Integer> {

    @Override
    public RecurrentPaymentTool create(MachineEvent event, long sequenceId, Integer id, RecurrentPaymentTool old,
                                       String occurredAt) {
        RecurrentPaymentTool recurrentPaymentTool = null;
        if (old != null) {
            recurrentPaymentTool = new RecurrentPaymentTool(old);
        } else {
            recurrentPaymentTool = new RecurrentPaymentTool();
        }
        recurrentPaymentTool.setId(null);
        recurrentPaymentTool.setWtime(null);
        recurrentPaymentTool.setChangeId(id);
        recurrentPaymentTool.setSequenceId((int) sequenceId);
        recurrentPaymentTool.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        return recurrentPaymentTool;
    }

    @Override
    public RecurrentPaymentTool create(MachineEvent event, long sequenceId, Integer id, String occurredAt) {
        return create(event, sequenceId, id, null, occurredAt);
    }

}
