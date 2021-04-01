package com.rbkmoney.newway.handler.event.stock.impl.invoicing.refund;

import com.rbkmoney.damsel.domain.TransactionInfo;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentRefundChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentSessionChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.invoicing.iface.RefundDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.Refund;
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
import com.rbkmoney.newway.handler.event.stock.impl.invoicing.InvoicingHandler;
import com.rbkmoney.newway.service.CashFlowService;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentRefundSessionChangeTransactionBoundHandler implements InvoicingHandler {

    private final RefundDao refundDao;
    private final CashFlowService cashFlowService;
    private final MachineEventCopyFactory<Refund, Integer> machineEventCopyFactory;

    @Getter
    private Filter filter = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change" +
                    ".payload.invoice_payment_refund_change.payload.invoice_payment_session_change" +
                    ".payload.session_transaction_bound",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(InvoiceChange change, MachineEvent event, Integer changeId) {
        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        String invoiceId = event.getSourceId();
        String paymentId = invoicePaymentChange.getId();
        InvoicePaymentRefundChange invoicePaymentRefundChange =
                invoicePaymentChange.getPayload().getInvoicePaymentRefundChange();
        String refundId = invoicePaymentRefundChange.getId();
        long sequenceId = event.getEventId();

        log.info("Start handling refund session change transaction info, " +
                        "sequenceId='{}', invoiceId='{}', paymentId='{}', refundId='{}'",
                sequenceId, invoiceId, paymentId, refundId);
        Refund refundOld = refundDao.get(invoiceId, paymentId, refundId);
        Refund refundNew = machineEventCopyFactory.create(event, sequenceId, changeId, refundOld, null);

        InvoicePaymentSessionChange sessionChange =
                invoicePaymentRefundChange.getPayload().getInvoicePaymentSessionChange();
        com.rbkmoney.damsel.payment_processing.SessionChangePayload payload = sessionChange.getPayload();
        TransactionInfo transactionInfo = payload.getSessionTransactionBound().getTrx();
        refundNew.setSessionPayloadTransactionBoundTrxId(transactionInfo.getId());
        refundNew
                .setSessionPayloadTransactionBoundTrxExtraJson(JsonUtil.objectToJsonString(transactionInfo.getExtra()));

        refundDao.save(refundNew).ifPresentOrElse(
                id -> {
                    Long oldId = refundOld.getId();
                    refundDao.updateNotCurrent(oldId);
                    cashFlowService.save(oldId, id, PaymentChangeType.refund);
                    log.info("Refund session transaction info has been saved, " +
                                    "sequenceId='{}', invoiceId='{}', paymentId='{}', refundId='{}'",
                            sequenceId, invoiceId, paymentId, refundId);
                },
                () -> log.info("Refund session transaction info bound duplicated, " +
                                "sequenceId={}, invoiceId={}, paymentId={}, refundId={}",
                        sequenceId, invoiceId, paymentId, refundId));
    }
}
