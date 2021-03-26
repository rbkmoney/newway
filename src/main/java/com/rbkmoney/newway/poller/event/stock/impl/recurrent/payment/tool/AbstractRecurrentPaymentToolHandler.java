package com.rbkmoney.newway.poller.event.stock.impl.recurrent.payment.tool;

import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.recurrent.payment.tool.iface.RecurrentPaymentToolDao;
import com.rbkmoney.newway.domain.tables.pojos.RecurrentPaymentTool;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event.stock.Handler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractRecurrentPaymentToolHandler implements Handler<RecurrentPaymentToolChange, MachineEvent> {

    private final RecurrentPaymentToolDao recurrentPaymentToolDao;

    protected RecurrentPaymentTool getRecurrentPaymentToolSource(MachineEvent event) {
        RecurrentPaymentTool recurrentPaymentTool = recurrentPaymentToolDao.get(event.getSourceId());
        if (recurrentPaymentTool == null) {
            throw new NotFoundException(
                    String.format("Recurrent payment tool not found, sourceId='%s'", event.getSourceId()));
        }
        return recurrentPaymentTool;
    }

    protected void saveAndUpdateNotCurrent(RecurrentPaymentTool recurrentPaymentTool, Long rptSourceId) {
        if (recurrentPaymentToolDao.save(recurrentPaymentTool).isPresent()) {
            recurrentPaymentToolDao.updateNotCurrent(rptSourceId);
        }
    }

    protected void setDefaultProperties(RecurrentPaymentTool recurrentPaymentTool, MachineEvent event,
                                        Integer changeId) {
        recurrentPaymentTool.setId(null);
        recurrentPaymentTool.setWtime(null);
        recurrentPaymentTool.setChangeId(changeId);
        recurrentPaymentTool.setSequenceId((int) event.getEventId());
        recurrentPaymentTool.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
    }
}
