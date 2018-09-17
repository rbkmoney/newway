package com.rbkmoney.newway.poller.event_stock.impl.invoicing.refund;

import com.rbkmoney.damsel.domain.InvoicePaymentRefund;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.dao.invoicing.iface.RefundDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.enums.RefundStatus;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.domain.tables.pojos.Refund;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.util.CashFlowUtil;
import com.rbkmoney.newway.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class InvoicePaymentRefundCreatedHandler extends AbstractInvoicingHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RefundDao refundDao;

    private final PaymentDao paymentDao;

    private final CashFlowDao cashFlowDao;

    private final Filter filter;

    @Autowired
    public InvoicePaymentRefundCreatedHandler(RefundDao refundDao, PaymentDao paymentDao, CashFlowDao cashFlowDao) {
        this.refundDao = refundDao;
        this.paymentDao = paymentDao;
        this.cashFlowDao = cashFlowDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "invoice_payment_change.payload.invoice_payment_refund_change.payload.invoice_payment_refund_created",
                new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(InvoiceChange invoiceChange, Event event) {
        long eventId = event.getId();
        String invoiceId = event.getSource().getInvoiceId();
        InvoicePaymentChange invoicePaymentChange = invoiceChange.getInvoicePaymentChange();
        String paymentId = invoicePaymentChange.getId();

        InvoicePaymentRefundChange invoicePaymentRefundChange = invoicePaymentChange.getPayload()
                .getInvoicePaymentRefundChange();
        InvoicePaymentRefundCreated invoicePaymentRefundCreated = invoicePaymentRefundChange.getPayload()
                .getInvoicePaymentRefundCreated();

        InvoicePaymentRefund invoicePaymentRefund = invoicePaymentRefundCreated.getRefund();

        String refundId = invoicePaymentRefund.getId();
        log.info("Start refund created handling, eventId={}, invoiceId={}, paymentId={}, refundId={}",
                eventId, invoiceId, paymentId, refundId);

        Refund refund = new Refund();
        refund.setEventId(eventId);
        refund.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        refund.setDomainRevision(invoicePaymentRefund.getDomainRevision());
        refund.setRefundId(refundId);
        refund.setPaymentId(paymentId);
        refund.setInvoiceId(invoiceId);

        Payment payment = paymentDao.get(invoiceId, paymentId);
        if (payment == null) {
            throw new NotFoundException(String.format("Payment on refund not found, invoiceId='%s', paymentId='%s', refundId='%s'",
                    invoiceId, paymentId, refundId));
        }

        refund.setPartyId(payment.getPartyId());
        refund.setShopId(payment.getShopId());
        refund.setCreatedAt(TypeUtil.stringToLocalDateTime(invoicePaymentRefund.getCreatedAt()));
        RefundStatus status = TypeUtil.toEnumField(invoicePaymentRefund.getStatus().getSetField().getFieldName(), RefundStatus.class);
        if (status == null) {
            throw new IllegalArgumentException("Illegal refund status: " + invoicePaymentRefund.getStatus());
        }
        refund.setStatus(status);
        if (invoicePaymentRefund.getStatus().isSetFailed()) {
            refund.setStatusFailedFailure(JsonUtil.toJsonString(invoicePaymentRefund.getStatus().getFailed()));
        }

        if (invoicePaymentRefund.isSetCash()) {
            refund.setAmount(invoicePaymentRefund.getCash().getAmount());
            refund.setCurrencyCode(invoicePaymentRefund.getCash().getCurrency().getSymbolicCode());
        } else {
            refund.setAmount(payment.getAmount());
            refund.setCurrencyCode(payment.getCurrencyCode());
        }
        refund.setReason(invoicePaymentRefund.getReason());

        long rfndId = refundDao.save(refund);

        List<CashFlow> cashFlowList = CashFlowUtil.convertCashFlows(invoicePaymentRefundCreated.getCashFlow(), rfndId, PaymentChangeType.refund);
        cashFlowDao.save(cashFlowList);

        log.info("Refund has been saved, eventId={}, invoiceId={}, paymentId={}, refundId={}",
                eventId, invoiceId, paymentId, refundId);
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
