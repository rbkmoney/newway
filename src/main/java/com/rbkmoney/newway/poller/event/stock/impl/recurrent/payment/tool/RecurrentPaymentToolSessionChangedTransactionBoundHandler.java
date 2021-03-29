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
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecurrentPaymentToolSessionChangedTransactionBoundHandler implements RecurrentPaymentToolHandler {

    private final RecurrentPaymentToolDao recurrentPaymentToolDao;
    private final MachineEventCopyFactory<RecurrentPaymentTool, Integer> recurrentPaymentToolCopyFactory;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("rec_payment_tool_session_changed.payload.session_transaction_bound",
                    new IsNullCondition().not()));

    @Override
    public void handle(RecurrentPaymentToolChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        log.info(
                "Start recurrent payment tool session changed transaction bound handling, " +
                        "sourceId={}, sequenceId={}, changeId={}",
                event.getSourceId(), sequenceId, changeId);
        final RecurrentPaymentTool recurrentPaymentToolOld = recurrentPaymentToolDao.getNotNull(event.getSourceId());
        RecurrentPaymentTool recurrentPaymentToolNew =
                recurrentPaymentToolCopyFactory.create(event, sequenceId, changeId, recurrentPaymentToolOld, null);
        TransactionInfo trx =
                change.getRecPaymentToolSessionChanged().getPayload().getSessionTransactionBound().getTrx();
        recurrentPaymentToolNew.setSessionPayloadTransactionBoundTrxId(trx.getId());
        recurrentPaymentToolNew
                .setSessionPayloadTransactionBoundTrxExtraJson(JsonUtil.objectToJsonString(trx.getExtra()));
        if (trx.isSetAdditionalInfo()) {
            recurrentPaymentToolNew
                    .setSessionPayloadTransactionBoundTrxAdditionalInfoRrn(trx.getAdditionalInfo().getRrn());
        }

        recurrentPaymentToolDao.save(recurrentPaymentToolNew).ifPresentOrElse(
                id -> {
                    recurrentPaymentToolDao.updateNotCurrent(recurrentPaymentToolOld.getId());
                    log.info("End recurrent payment tool session changed transaction handling, " +
                            "sourceId={}, sequenceId={}, changeId={}", event.getSourceId(), sequenceId, changeId);
                },
                () -> log.info("End recurrent payment tool session changed transaction bound duplicated, " +
                        "sequenceId={}, changeId={}", sequenceId, changeId));
    }

}
