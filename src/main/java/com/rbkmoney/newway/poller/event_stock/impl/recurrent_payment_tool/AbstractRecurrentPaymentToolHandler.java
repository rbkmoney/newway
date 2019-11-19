package com.rbkmoney.newway.poller.event_stock.impl.recurrent_payment_tool;

import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolChange;
import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolEvent;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.newway.dao.recurrent_payment_tool.iface.RecurrentPaymentToolDao;
import com.rbkmoney.newway.domain.tables.pojos.RecurrentPaymentTool;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.Handler;

public abstract class AbstractRecurrentPaymentToolHandler implements Handler<RecurrentPaymentToolChange, RecurrentPaymentToolEvent> {

    private RecurrentPaymentToolDao recurrentPaymentToolDao;

    public AbstractRecurrentPaymentToolHandler(RecurrentPaymentToolDao recurrentPaymentToolDao) {
        this.recurrentPaymentToolDao = recurrentPaymentToolDao;
    }

    protected RecurrentPaymentTool getRecurrentPaymentToolSource(RecurrentPaymentToolEvent event) {
        RecurrentPaymentTool recurrentPaymentTool = recurrentPaymentToolDao.get(event.getSource());
        if (recurrentPaymentTool == null) {
            throw new NotFoundException(String.format("Recurrent payment tool not found, recurrent_payment_tool_id='%s'", event.getSource()));
        }
        return recurrentPaymentTool;
    }

    protected void saveAndUpdateNotCurrent(RecurrentPaymentTool recurrentPaymentTool) {
        Long rptId = recurrentPaymentToolDao.save(recurrentPaymentTool);
        if (rptId != null) {
            recurrentPaymentToolDao.updateNotCurrent(rptId);
        }
    }

    protected void setDefaultProperties(RecurrentPaymentTool recurrentPaymentTool, RecurrentPaymentToolEvent event, Integer changeId) {
        recurrentPaymentTool.setId(null);
        recurrentPaymentTool.setWtime(null);
        recurrentPaymentTool.setCurrent(false);
        recurrentPaymentTool.setEventId(event.getId());
        recurrentPaymentTool.setChangeId(changeId);
        recurrentPaymentTool.setSequenceId(event.getSequence());
        recurrentPaymentTool.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
    }
}
