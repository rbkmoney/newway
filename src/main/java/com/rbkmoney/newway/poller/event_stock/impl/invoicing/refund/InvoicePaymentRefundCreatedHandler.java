package com.rbkmoney.newway.poller.event_stock.impl.invoicing.refund;

import com.rbkmoney.damsel.domain.InvoicePaymentRefund;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentRefundChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentRefundCreated;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.invoicing.iface.BatchDao;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.dao.invoicing.iface.RefundDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.enums.RefundStatus;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.domain.tables.pojos.Refund;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.model.InvoicingKey;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.util.CashFlowUtil;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentRefundCreatedHandler extends AbstractInvoicingHandler {

    private final RefundDao refundDao;
    private final BatchDao<Payment> paymentDao;
    private final CashFlowDao cashFlowDao;

    private Filter filter = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change.payload.invoice_payment_refund_change.payload.invoice_payment_refund_created",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(InvoiceChange invoiceChange, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        String invoiceId = event.getSourceId();
        InvoicePaymentChange invoicePaymentChange = invoiceChange.getInvoicePaymentChange();
        String paymentId = invoicePaymentChange.getId();

        InvoicePaymentRefundChange invoicePaymentRefundChange = invoicePaymentChange.getPayload()
                .getInvoicePaymentRefundChange();
        InvoicePaymentRefundCreated invoicePaymentRefundCreated = invoicePaymentRefundChange.getPayload()
                .getInvoicePaymentRefundCreated();

        InvoicePaymentRefund invoicePaymentRefund = invoicePaymentRefundCreated.getRefund();

        String refundId = invoicePaymentRefund.getId();
        log.info("Start refund created handling, sequenceId={}, invoiceId={}, paymentId={}, refundId={}",
                sequenceId, invoiceId, paymentId, refundId);

        Refund refund = new Refund();
        refund.setChangeId(changeId);
        refund.setSequenceId(sequenceId);
        refund.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        refund.setDomainRevision(invoicePaymentRefund.getDomainRevision());
        refund.setRefundId(refundId);
        refund.setPaymentId(paymentId);
        refund.setInvoiceId(invoiceId);
        refund.setExternalId(invoicePaymentRefund.getExternalId());

        Payment payment = paymentDao.get(InvoicingKey.buildKey(invoiceId, paymentId));
        if (payment == null) {
            throw new NotFoundException(String.format("Payment on refund not found, invoiceId='%s', paymentId='%s', refundId='%s'",
                    invoiceId, paymentId, refundId));
        }

        refund.setPartyId(payment.getPartyId());
        refund.setShopId(payment.getShopId());
        refund.setCreatedAt(TypeUtil.stringToLocalDateTime(invoicePaymentRefund.getCreatedAt()));
        refund.setStatus(TBaseUtil.unionFieldToEnum(invoicePaymentRefund.getStatus(), RefundStatus.class));
        if (invoicePaymentRefund.getStatus().isSetFailed()) {
            refund.setStatusFailedFailure(JsonUtil.tBaseToJsonString(invoicePaymentRefund.getStatus().getFailed()));
        }

        if (invoicePaymentRefund.isSetCash()) {
            refund.setAmount(invoicePaymentRefund.getCash().getAmount());
            refund.setCurrencyCode(invoicePaymentRefund.getCash().getCurrency().getSymbolicCode());
        } else {
            refund.setAmount(payment.getAmount());
            refund.setCurrencyCode(payment.getCurrencyCode());
        }
        refund.setReason(invoicePaymentRefund.getReason());
        if (invoicePaymentRefund.isSetPartyRevision()) {
            refund.setPartyRevision(invoicePaymentRefund.getPartyRevision());
        }

        Long rfndId = refundDao.save(refund);
        if (rfndId != null) {
            List<CashFlow> cashFlowList = CashFlowUtil.convertCashFlows(invoicePaymentRefundCreated.getCashFlow(), rfndId, PaymentChangeType.refund);
            cashFlowDao.save(cashFlowList);
            refundDao.updateCommissions(rfndId);
        }

        log.info("Refund has been saved, sequenceId={}, invoiceId={}, paymentId={}, refundId={}",
                sequenceId, invoiceId, paymentId, refundId);
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
