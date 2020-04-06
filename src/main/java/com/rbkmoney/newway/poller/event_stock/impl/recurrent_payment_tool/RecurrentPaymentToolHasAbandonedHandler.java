package com.rbkmoney.newway.poller.event_stock.impl.recurrent_payment_tool;

import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.recurrent_payment_tool.iface.RecurrentPaymentToolDao;
import com.rbkmoney.newway.domain.enums.RecurrentPaymentToolStatus;
import com.rbkmoney.newway.domain.tables.pojos.RecurrentPaymentTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RecurrentPaymentToolHasAbandonedHandler extends AbstractRecurrentPaymentToolHandler {

    private final Filter filter;

    public RecurrentPaymentToolHasAbandonedHandler(RecurrentPaymentToolDao recurrentPaymentToolDao) {
        super(recurrentPaymentToolDao);
        this.filter = new PathConditionFilter(
                new PathConditionRule("rec_payment_tool_abandoned", new IsNullCondition().not()));
    }

    @Override
    public void handle(RecurrentPaymentToolChange change, MachineEvent event, Integer changeId) {
        log.info("Start recurrent payment tool abandoned handling, sourceId={}, sequenceId={}, changeId={}",
                event.getSourceId(), event.getEventId(), changeId);
        RecurrentPaymentTool recurrentPaymentTool = getRecurrentPaymentToolSource(event);
        Long rptSourceId = recurrentPaymentTool.getId();
        setDefaultProperties(recurrentPaymentTool, event, changeId);
        recurrentPaymentTool.setStatus(RecurrentPaymentToolStatus.abandoned);
        saveAndUpdateNotCurrent(recurrentPaymentTool, rptSourceId);
        log.info("End recurrent payment tool abandoned handling, sourceId={}, sequenceId={}, changeId={}",
                event.getSourceId(), event.getEventId(), changeId);
    }

    @Override
    public Filter<RecurrentPaymentToolChange> getFilter() {
        return filter;
    }
}
