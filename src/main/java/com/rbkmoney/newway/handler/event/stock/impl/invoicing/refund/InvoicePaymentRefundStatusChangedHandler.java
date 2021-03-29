package com.rbkmoney.newway.handler.event.stock.impl.invoicing.refund;

import com.rbkmoney.damsel.domain.InvoicePaymentRefundStatus;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentRefundChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
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
public class InvoicePaymentRefundStatusChangedHandler implements InvoicingHandler {

    private final RefundDao refundDao;
    private final CashFlowService cashFlowService;
    private final MachineEventCopyFactory<Refund, Integer> machineEventCopyFactory;

    @Getter
    private Filter filter = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change" +
                    ".payload.invoice_payment_refund_change.payload.invoice_payment_refund_status_changed",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(InvoiceChange invoiceChange, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        String invoiceId = event.getSourceId();
        InvoicePaymentChange invoicePaymentChange = invoiceChange.getInvoicePaymentChange();
        String paymentId = invoiceChange.getInvoicePaymentChange().getId();
        InvoicePaymentRefundChange invoicePaymentRefundChange = invoicePaymentChange.getPayload()
                .getInvoicePaymentRefundChange();
        InvoicePaymentRefundStatus invoicePaymentRefundStatus =
                invoicePaymentRefundChange.getPayload().getInvoicePaymentRefundStatusChanged().getStatus();
        String refundId = invoicePaymentRefundChange.getId();

        log.info(
                "Start refund status changed handling, " +
                        "sequenceId={}, invoiceId={}, paymentId={}, refundId={}, status={}",
                sequenceId, invoiceId, paymentId, refundId, invoicePaymentRefundStatus.getSetField().getFieldName());
        Refund refundOld = refundDao.get(invoiceId, paymentId, refundId);
        if (refundOld == null) {
            throw new NotFoundException(String.format("Refund not found, " +
                            "invoiceId='%s', paymentId='%s', refundId='%s'",
                    invoiceId, paymentId, refundId));
        }
        Refund refundNew = machineEventCopyFactory.create(event, sequenceId, changeId, refundOld, null);

        refundNew.setStatus(TBaseUtil.unionFieldToEnum(invoicePaymentRefundStatus, RefundStatus.class));
        if (invoicePaymentRefundStatus.isSetFailed()) {
            refundNew.setStatusFailedFailure(JsonUtil.thriftBaseToJsonString(invoicePaymentRefundStatus.getFailed()));
        } else {
            refundNew.setStatusFailedFailure(null);
        }

        refundDao.save(refundNew).ifPresentOrElse(
                id -> {
                    Long oldId = refundOld.getId();
                    refundDao.updateNotCurrent(oldId);
                    cashFlowService.save(oldId, id, PaymentChangeType.refund);
                    log.info("Refund status changed has been saved, " +
                                    "sequenceId='{}', invoiceId='{}', paymentId='{}', refundId='{}'",
                            sequenceId, invoiceId, paymentId, refundId);
                },
                () -> log.info("Refund status changed bound duplicated, " +
                                "sequenceId={}, invoiceId={}, paymentId={}, refundId={}",
                        sequenceId, invoiceId, paymentId, refundId));
    }

}
