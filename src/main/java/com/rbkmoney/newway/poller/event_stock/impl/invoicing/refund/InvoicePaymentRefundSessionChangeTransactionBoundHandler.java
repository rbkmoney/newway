package com.rbkmoney.newway.poller.event_stock.impl.invoicing.refund;

import com.rbkmoney.damsel.domain.TransactionInfo;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.dao.invoicing.iface.RefundDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Refund;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class InvoicePaymentRefundSessionChangeTransactionBoundHandler extends AbstractInvoicingHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RefundDao refundDao;

    private final CashFlowDao cashFlowDao;

    private final Filter filter;

    @Autowired
    public InvoicePaymentRefundSessionChangeTransactionBoundHandler(RefundDao refundDao, CashFlowDao cashFlowDao) {
        this.refundDao = refundDao;
        this.cashFlowDao = cashFlowDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "invoice_payment_change.payload.invoice_payment_refund_change.payload.invoice_payment_session_change.payload.session_transaction_bound",
                new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(InvoiceChange change, Event event) {
        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        String invoiceId = event.getSource().getInvoiceId();
        String paymentId = invoicePaymentChange.getId();
        InvoicePaymentRefundChange invoicePaymentRefundChange = invoicePaymentChange.getPayload().getInvoicePaymentRefundChange();
        String refundId = invoicePaymentRefundChange.getId();
        InvoicePaymentSessionChange sessionChange = invoicePaymentRefundChange.getPayload().getInvoicePaymentSessionChange();
        log.info("Start handling refund session change transaction info, eventId='{}', invoiceId='{}', paymentId='{}', refundId='{}'", event.getId(), invoiceId, paymentId, refundId);
        Refund refundSource = refundDao.get(invoiceId, paymentId, refundId);
        if (refundSource == null) {
            throw new NotFoundException(String.format("Refund not found, invoiceId='%s', paymentId='%s', refundId='%s'",
                    invoiceId, paymentId, refundId));
        }
        Long refundSourceId = refundSource.getId();
        refundSource.setId(null);
        refundSource.setWtime(null);
        refundSource.setEventId(event.getId());
        refundSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        com.rbkmoney.damsel.payment_processing.SessionChangePayload payload = sessionChange.getPayload();
        TransactionInfo transactionInfo = payload.getSessionTransactionBound().getTrx();
        refundSource.setSessionPayloadTransactionBoundTrxId(transactionInfo.getId());
        refundSource.setSessionPayloadTransactionBoundTrxExtraJson(JsonUtil.objectToJsonString(transactionInfo.getExtra()));
        refundDao.updateNotCurrent(invoiceId, paymentId, refundId);
        long rfndId = refundDao.save(refundSource);
        List<CashFlow> cashFlows = cashFlowDao.getByObjId(refundSourceId, PaymentChangeType.refund);
        cashFlows.forEach(pcf -> {
            pcf.setId(null);
            pcf.setObjId(rfndId);
        });
        cashFlowDao.save(cashFlows);
        log.info("Refund session transaction info has been saved, eventId='{}', invoiceId='{}', paymentId='{}', refundId='{}'", event.getId(), invoiceId, paymentId, refundId);
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
