package com.rbkmoney.newway.poller.event.stock.impl.invoicing.refund;

import com.rbkmoney.damsel.domain.TransactionInfo;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.invoicing.iface.RefundDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.Refund;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event.stock.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.service.CashFlowService;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentRefundSessionChangeTransactionBoundHandler extends AbstractInvoicingHandler {

    private final RefundDao refundDao;
    private final CashFlowService cashFlowService;

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

        log.info(
                "Start handling refund session change transaction info, " +
                        "sequenceId='{}', invoiceId='{}', paymentId='{}', refundId='{}'",
                sequenceId, invoiceId, paymentId, refundId);
        Refund refundSource = refundDao.get(invoiceId, paymentId, refundId);
        if (refundSource == null) {
            throw new NotFoundException(String.format("Refund not found, " +
                            "invoiceId='%s', paymentId='%s', refundId='%s'",
                    invoiceId, paymentId, refundId));
        }
        Long refundSourceId = refundSource.getId();
        refundSource.setId(null);
        refundSource.setWtime(null);
        refundSource.setChangeId(changeId);
        refundSource.setSequenceId(sequenceId);
        refundSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        InvoicePaymentSessionChange sessionChange =
                invoicePaymentRefundChange.getPayload().getInvoicePaymentSessionChange();
        com.rbkmoney.damsel.payment_processing.SessionChangePayload payload = sessionChange.getPayload();
        TransactionInfo transactionInfo = payload.getSessionTransactionBound().getTrx();
        refundSource.setSessionPayloadTransactionBoundTrxId(transactionInfo.getId());
        refundSource
                .setSessionPayloadTransactionBoundTrxExtraJson(JsonUtil.objectToJsonString(transactionInfo.getExtra()));
        Long rfndId = refundDao.save(refundSource);
        if (rfndId != null) {
            refundDao.updateNotCurrent(refundSourceId);
            cashFlowService.save(refundSourceId, rfndId, PaymentChangeType.refund);
        }
        log.info(
                "Refund session transaction info has been saved, " +
                        "sequenceId='{}', invoiceId='{}', paymentId='{}', refundId='{}'",
                sequenceId, invoiceId, paymentId, refundId);
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
