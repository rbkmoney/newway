package com.rbkmoney.newway.poller.event_stock.impl.invoicing.payment;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentCaptureParams;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentCaptureStarted;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;

import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.model.PaymentWrapper;
import com.rbkmoney.newway.poller.event_stock.LocalStorage;
import com.rbkmoney.newway.service.PaymentWrapperService;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentCaptureStartedMapper extends AbstractInvoicingPaymentMapper {

    private final PaymentWrapperService paymentWrapperService;

    private final Filter filter = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change.payload.invoice_payment_capture_started",
            new IsNullCondition().not()));

    @Override
    public PaymentWrapper map(InvoiceChange change, MachineEvent event, Integer changeId, LocalStorage storage) {
        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        InvoicePaymentCaptureStarted invoicePaymentCaptureStarted = change.getInvoicePaymentChange().getPayload().getInvoicePaymentCaptureStarted();

        long sequenceId = event.getEventId();
        String paymentId = invoicePaymentChange.getId();
        String invoiceId = event.getSourceId();
        InvoicePaymentCaptureParams captureParams = invoicePaymentCaptureStarted.getParams();
        log.info("Start payment capture started handling, sequenceId={}, invoiceId={}, paymentId={}", sequenceId, invoiceId, paymentId);

        PaymentWrapper paymentWrapper = paymentWrapperService.get(invoiceId, paymentId, storage);
        Payment paymentSource = paymentWrapper.getPayment();
        setDefaultProperties(paymentSource, sequenceId, changeId, event.getCreatedAt());
        if (captureParams.isSetCash()) {
            paymentSource.setAmount(captureParams.getCash().getAmount());
            paymentSource.setCurrencyCode(captureParams.getCash().getCurrency().getSymbolicCode());
        }
        paymentSource.setStatusCapturedStartedReason(captureParams.getReason());
        if (captureParams.isSetCart()) {
            String cartsJson = JsonUtil.objectToJsonString(captureParams.getCart().getLines().stream().map(JsonUtil::tBaseToJsonNode).collect(Collectors.toList()));
            paymentSource.setCaptureStartedParamsCartJson(cartsJson);
        }
        log.info("Payment has been saved, sequenceId={}, invoiceId={}, paymentId={}", sequenceId, invoiceId, paymentSource.getId());
        return paymentWrapper;
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
