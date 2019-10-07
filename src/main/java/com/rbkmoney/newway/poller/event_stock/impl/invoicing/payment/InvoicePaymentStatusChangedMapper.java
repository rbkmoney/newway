package com.rbkmoney.newway.poller.event_stock.impl.invoicing.payment;

import com.rbkmoney.damsel.domain.InvoicePaymentCaptured;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.enums.PaymentStatus;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.poller.event_stock.*;
import com.rbkmoney.newway.model.PaymentWrapper;
import com.rbkmoney.newway.service.PaymentWrapperService;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentStatusChangedMapper extends AbstractInvoicingPaymentMapper {

    private final PaymentWrapperService paymentWrapperService;

    private Filter filter = new PathConditionFilter(
            new PathConditionRule("invoice_payment_change.payload.invoice_payment_status_changed", new IsNullCondition().not()));

    @Override
    public PaymentWrapper map(InvoiceChange change, MachineEvent event, Integer changeId, LocalStorage storage) {
        InvoicePaymentStatus invoicePaymentStatus = change.getInvoicePaymentChange().getPayload().getInvoicePaymentStatusChanged().getStatus();
        long sequenceId = event.getEventId();
        String invoiceId = event.getSourceId();
        String paymentId = change.getInvoicePaymentChange().getId();

        log.info("Start payment status changed mapping, sequenceId={}, invoiceId={}, paymentId={}, status={}",
                sequenceId, invoiceId, paymentId, invoicePaymentStatus.getSetField().getFieldName());

        PaymentWrapper paymentWrapper = paymentWrapperService.get(invoiceId, paymentId, storage);
        Payment paymentSource = paymentWrapper.getPayment();
        setDefaultProperties(paymentSource, sequenceId, changeId, event.getCreatedAt());
        paymentSource.setStatus(TBaseUtil.unionFieldToEnum(invoicePaymentStatus, PaymentStatus.class));
        if (invoicePaymentStatus.isSetCancelled()) {
            paymentSource.setStatusCancelledReason(invoicePaymentStatus.getCancelled().getReason());
            paymentSource.setStatusCapturedReason(null);
            paymentSource.setStatusFailedFailure(null);
        } else if (invoicePaymentStatus.isSetCaptured()) {
            paymentSource.setStatusCancelledReason(null);
            InvoicePaymentCaptured invoicePaymentCaptured = invoicePaymentStatus.getCaptured();
            paymentSource.setStatusCapturedReason(invoicePaymentCaptured.getReason());
            if (invoicePaymentCaptured.isSetCost()) {
                paymentSource.setAmount(invoicePaymentCaptured.getCost().getAmount());
                paymentSource.setCurrencyCode(invoicePaymentCaptured.getCost().getCurrency().getSymbolicCode());
            }
            paymentSource.setStatusFailedFailure(null);
        } else if (invoicePaymentStatus.isSetFailed()) {
            paymentSource.setStatusCancelledReason(null);
            paymentSource.setStatusCapturedReason(null);
            paymentSource.setStatusFailedFailure(JsonUtil.tBaseToJsonString(invoicePaymentStatus.getFailed()));
        }
        log.info("Payment status has been mapped, sequenceId={}, invoiceId={}, paymentId={}, status={}",
                sequenceId, invoiceId, paymentId, invoicePaymentStatus.getSetField().getFieldName());
        return paymentWrapper;
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
