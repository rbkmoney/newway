package com.rbkmoney.newway.poller.event_stock.impl.invoicing.refund;

import com.rbkmoney.damsel.domain.InvoicePaymentRefundStatus;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.invoicing.iface.RefundDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.enums.RefundStatus;
import com.rbkmoney.newway.domain.tables.pojos.Refund;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingHandler;
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
public class InvoicePaymentRefundStatusChangedHandler extends AbstractInvoicingHandler {

    private final RefundDao refundDao;
    private final CashFlowService cashFlowService;

    private Filter filter = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change.payload.invoice_payment_refund_change.payload.invoice_payment_refund_status_changed",
            new IsNullCondition().not()));
    ;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(InvoiceChange invoiceChange, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        String invoiceId = event.getSourceId();
        InvoicePaymentChange invoicePaymentChange = invoiceChange.getInvoicePaymentChange();
        String paymentId = invoiceChange.getInvoicePaymentChange().getId();
        InvoicePaymentRefundChange invoicePaymentRefundChange = invoicePaymentChange.getPayload()
                .getInvoicePaymentRefundChange();
        InvoicePaymentRefundStatus invoicePaymentRefundStatus = invoicePaymentRefundChange.getPayload().getInvoicePaymentRefundStatusChanged().getStatus();
        String refundId = invoicePaymentRefundChange.getId();

        log.info("Start refund status changed handling, sequenceId={}, invoiceId={}, paymentId={}, refundId={}, status={}",
                sequenceId, invoiceId, paymentId, refundId, invoicePaymentRefundStatus.getSetField().getFieldName());
        Refund refundSource = refundDao.get(invoiceId, paymentId, refundId);
        if (refundSource == null) {
            throw new NotFoundException(String.format("Refund not found, invoiceId='%s', paymentId='%s', refundId='%s'",
                    invoiceId, paymentId, refundId));
        }
        Long refundSourceId = refundSource.getId();
        refundSource.setId(null);
        refundSource.setWtime(null);
        refundSource.setChangeId(changeId);
        refundSource.setSequenceId(sequenceId);
        refundSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        refundSource.setStatus(TBaseUtil.unionFieldToEnum(invoicePaymentRefundStatus, RefundStatus.class));
        if (invoicePaymentRefundStatus.isSetFailed()) {
            refundSource.setStatusFailedFailure(JsonUtil.tBaseToJsonString(invoicePaymentRefundStatus.getFailed()));
        } else {
            refundSource.setStatusFailedFailure(null);
        }
        Long rfndId = refundDao.save(refundSource);
        if (rfndId != null) {
            refundDao.updateNotCurrent(refundSourceId);
            cashFlowService.save(refundSourceId, rfndId, PaymentChangeType.refund);
        }

        log.info("Refund have been succeeded, sequenceId={}, invoiceId={}, paymentId={}, refundId={}, status={}",
                sequenceId, invoiceId, paymentId, refundId, invoicePaymentRefundStatus.getSetField().getFieldName());
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
