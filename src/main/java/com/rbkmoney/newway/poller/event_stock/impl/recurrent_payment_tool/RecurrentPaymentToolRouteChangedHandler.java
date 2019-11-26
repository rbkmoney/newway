package com.rbkmoney.newway.poller.event_stock.impl.recurrent_payment_tool;

import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolChange;
import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolEvent;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.recurrent_payment_tool.iface.RecurrentPaymentToolDao;
import com.rbkmoney.newway.domain.tables.pojos.RecurrentPaymentTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class RecurrentPaymentToolRouteChangedHandler extends AbstractRecurrentPaymentToolHandler {

    private final Filter filter;

    public RecurrentPaymentToolRouteChangedHandler(RecurrentPaymentToolDao recurrentPaymentToolDao) {
        super(recurrentPaymentToolDao);
        this.filter = new PathConditionFilter(
                new PathConditionRule("rec_payment_tool_route_changed", new IsNullCondition().not()));
    }

    @Override
    @Transactional
    public void handle(RecurrentPaymentToolChange change, RecurrentPaymentToolEvent event, Integer changeId) {
        log.info("Start recurrent payment tool route changed handling, eventId={}, recurrent_payment_tool_id={}", event.getId(), event.getSource());
        RecurrentPaymentTool recurrentPaymentTool = getRecurrentPaymentToolSource(event);
        Long rptSourceId = recurrentPaymentTool.getId();
        setDefaultProperties(recurrentPaymentTool, event, changeId);
        recurrentPaymentTool.setRouteProviderId(change.getRecPaymentToolRouteChanged().getRoute().getProvider().getId());
        recurrentPaymentTool.setRouteTerminalId(change.getRecPaymentToolRouteChanged().getRoute().getTerminal().getId());
        saveAndUpdateNotCurrent(recurrentPaymentTool, rptSourceId);
        log.info("End recurrent payment tool route changed handling, eventId={}, recurrent_payment_tool_id={}", event.getId(), event.getSource());
    }

    @Override
    public Filter<RecurrentPaymentToolChange> getFilter() {
        return filter;
    }
}
