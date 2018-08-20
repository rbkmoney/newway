package com.rbkmoney.newway.poller.handler.impl.invoicing.refund;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.domain.InvoicePaymentRefundStatus;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentRefundChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.dao.invoicing.iface.RefundDao;
import com.rbkmoney.newway.domain.enums.Paymentchangetype;
import com.rbkmoney.newway.domain.enums.Refundstatus;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Refund;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.handler.impl.invoicing.AbstractInvoicingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvoicePaymentRefundStatusChangedHandler extends AbstractInvoicingHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RefundDao refundDao;

    private final CashFlowDao cashFlowDao;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Filter filter;

    @Autowired
    public InvoicePaymentRefundStatusChangedHandler(RefundDao refundDao, CashFlowDao cashFlowDao) {
        this.refundDao = refundDao;
        this.cashFlowDao = cashFlowDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "invoice_payment_change.payload.invoice_payment_refund_change.payload.invoice_payment_refund_status_changed",
                new IsNullCondition().not()));
    }

    @Override
    public void handle(InvoiceChange invoiceChange, Event event) {
        long eventId = event.getId();
        String invoiceId = event.getSource().getInvoiceId();
        InvoicePaymentChange invoicePaymentChange = invoiceChange.getInvoicePaymentChange();
        String paymentId = invoiceChange.getInvoicePaymentChange().getId();
        InvoicePaymentRefundChange invoicePaymentRefundChange = invoicePaymentChange.getPayload()
                .getInvoicePaymentRefundChange();
        InvoicePaymentRefundStatus invoicePaymentRefundStatus = invoicePaymentRefundChange.getPayload().getInvoicePaymentRefundStatusChanged().getStatus();
        String refundId = invoicePaymentRefundChange.getId();

        log.info("Start refund status changed handling, eventId={}, invoiceId={}, paymentId={}, refundId={}, status={}",
                eventId, invoiceId, paymentId, refundId, invoicePaymentRefundStatus.getSetField().getFieldName());
        Refund refundSource = refundDao.get(invoiceId, paymentId, refundId);
        if (refundSource == null) {
            throw new NotFoundException(String.format("Refund not found, invoiceId='%s', paymentId='%s', refundId='%s'",
                    invoiceId, paymentId, refundId));
        }
        Long refundSourceId = refundSource.getId();
        refundSource.setId(null);
        refundSource.setEventId(eventId);
        refundSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        Refundstatus status = TypeUtil.toEnumField(invoicePaymentRefundStatus.getSetField().getFieldName(), Refundstatus.class);
        if (status == null) {
            throw new IllegalArgumentException("Illegal payment status: " + invoicePaymentRefundStatus);
        }
        refundSource.setStatus(status);
        if (invoicePaymentRefundStatus.isSetFailed()) {
            try {
                refundSource.setStatusFailedFailure(objectMapper.writeValueAsString(invoicePaymentRefundStatus.getFailed()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            refundSource.setStatusFailedFailure(null);
        }
        refundDao.update(invoiceId, paymentId, refundId);
        long rfndId = refundDao.save(refundSource);
        List<CashFlow> cashFlows = cashFlowDao.getByObjId(refundSourceId, Paymentchangetype.refund);
        cashFlows.forEach(pcf -> {
            pcf.setId(null);
            pcf.setObjId(rfndId);
        });
        cashFlowDao.save(cashFlows);

        log.info("Refund have been succeeded, eventId={}, invoiceId={}, paymentId={}, refundId={}, status={}",
                eventId, invoiceId, paymentId, refundId, invoicePaymentRefundStatus.getSetField().getFieldName());
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
