package com.rbkmoney.newway.poller.event.stock.impl.recurrent.payment.tool;

import com.rbkmoney.damsel.domain.TransactionInfo;
import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.recurrent.payment.tool.iface.RecurrentPaymentToolDao;
import com.rbkmoney.newway.domain.tables.pojos.RecurrentPaymentTool;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@SuppressWarnings("VariableDeclarationUsageDistance")
public class RecurrentPaymentToolSessionChangedTransactionBoundHandler extends AbstractRecurrentPaymentToolHandler {

    private final Filter filter;

    public RecurrentPaymentToolSessionChangedTransactionBoundHandler(RecurrentPaymentToolDao recurrentPaymentToolDao) {
        super(recurrentPaymentToolDao);
        this.filter = new PathConditionFilter(
                new PathConditionRule("rec_payment_tool_session_changed.payload.session_transaction_bound",
                        new IsNullCondition().not()));
    }

    @Override
    public void handle(RecurrentPaymentToolChange change, MachineEvent event, Integer changeId) {
        log.info(
                "Start recurrent payment tool session changed transaction bound handling, " +
                        "sourceId={}, sequenceId={}, changeId={}",
                event.getSourceId(), event.getEventId(), changeId);
        RecurrentPaymentTool recurrentPaymentTool = getRecurrentPaymentToolSource(event);
        Long rptSourceId = recurrentPaymentTool.getId();
        setDefaultProperties(recurrentPaymentTool, event, changeId);
        TransactionInfo trx =
                change.getRecPaymentToolSessionChanged().getPayload().getSessionTransactionBound().getTrx();
        recurrentPaymentTool.setSessionPayloadTransactionBoundTrxId(trx.getId());
        recurrentPaymentTool.setSessionPayloadTransactionBoundTrxExtraJson(JsonUtil.objectToJsonString(trx.getExtra()));
        if (trx.isSetAdditionalInfo()) {
            recurrentPaymentTool
                    .setSessionPayloadTransactionBoundTrxAdditionalInfoRrn(trx.getAdditionalInfo().getRrn());
        }
        saveAndUpdateNotCurrent(recurrentPaymentTool, rptSourceId);
        log.info(
                "End recurrent payment tool session changed transaction bound handling, " +
                        "sourceId={}, sequenceId={}, changeId={}",
                event.getSourceId(), event.getEventId(), changeId);
    }

    @Override
    public Filter<RecurrentPaymentToolChange> getFilter() {
        return filter;
    }
}
