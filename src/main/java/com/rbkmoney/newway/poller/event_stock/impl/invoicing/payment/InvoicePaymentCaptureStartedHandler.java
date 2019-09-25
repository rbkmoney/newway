package com.rbkmoney.newway.poller.event_stock.impl.invoicing.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentCaptureParams;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentCaptureStarted;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceCartDao;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.enums.PaymentStatus;
import com.rbkmoney.newway.domain.tables.pojos.InvoiceCart;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.service.CashFlowService;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentCaptureStartedHandler extends AbstractInvoicingHandler {

    private final CashFlowService cashFlowService;

    private final PaymentDao paymentDao;

    private final InvoiceCartDao invoiceCartDao;

    private final Filter filter = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change.payload.invoice_payment_capture_started",
            new IsNullCondition().not()));


    @Override
    public void handle(InvoiceChange change, MachineEvent event, Integer changeId) {
        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        InvoicePaymentCaptureStarted invoicePaymentCaptureStarted = change.getInvoicePaymentChange()
                .getPayload()
                .getInvoicePaymentCaptureStarted();

        long sequenceId = event.getEventId();
        String paymentId = invoicePaymentChange.getId();
        String invoiceId = event.getSourceId();

        InvoicePaymentCaptureParams invoicePaymentCaptureStartedParams = invoicePaymentCaptureStarted.getParams();

        log.info("Start payment capture started handling, sequenceId={}, invoiceId={}, paymentId={}",
                sequenceId, invoiceId, paymentId);
        Payment paymentSource = paymentDao.get(invoiceId, paymentId);
        if (paymentSource == null) {
            throw new NotFoundException(String.format("Payment not found, invoiceId='%s', paymentId='%s'",
                    invoiceId, paymentId));
        }
        Long paymentSourceId = paymentSource.getId();
        paymentSource.setId(null);
        paymentSource.setWtime(null);
        paymentSource.setChangeId(changeId);
        paymentSource.setSequenceId(sequenceId);
        paymentSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));

        if (invoicePaymentCaptureStartedParams.isSetCash()) {
            paymentSource.setAmount(invoicePaymentCaptureStartedParams.getCash().getAmount());
            paymentSource.setCurrencyCode(invoicePaymentCaptureStartedParams.getCash().getCurrency().getSymbolicCode());
        }
        paymentSource.setStatusCapturedStartedReason(invoicePaymentCaptureStartedParams.getReason());

        Long pmntId = paymentDao.save(paymentSource);
        if (pmntId != null) {
            paymentDao.updateNotCurrent(paymentSourceId);
            cashFlowService.save(paymentSourceId, pmntId, PaymentChangeType.payment);
        }

        if (invoicePaymentCaptureStartedParams.isSetCart()) {
            List<InvoiceCart> invoiceCarts = invoicePaymentCaptureStartedParams.getCart().getLines().stream()
                    .map(il -> {
                        InvoiceCart ic = new InvoiceCart();
                        ic.setInvId(Long.valueOf(paymentSource.getInvoiceId()));
                        ic.setProduct(il.getProduct());
                        ic.setQuantity(il.getQuantity());
                        ic.setAmount(il.getPrice().getAmount());
                        ic.setCurrencyCode(il.getPrice().getCurrency().getSymbolicCode());
                        Map<String, JsonNode> jsonNodeMap = il.getMetadata().entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, e -> JsonUtil.tBaseToJsonNode(e.getValue())));
                        ic.setMetadataJson(JsonUtil.objectToJsonString(jsonNodeMap));
                        return ic;
                    }).collect(Collectors.toList());

            invoiceCartDao.save(invoiceCarts);
        }

        log.info("Payment has been saved, sequenceId={}, invoiceId={}, paymentId={}", sequenceId, invoiceId, paymentSource.getId());
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
