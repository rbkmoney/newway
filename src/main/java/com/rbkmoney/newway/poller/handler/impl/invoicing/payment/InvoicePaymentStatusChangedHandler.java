package com.rbkmoney.newway.poller.handler.impl.invoicing.payment;

import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.enums.PaymentStatus;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.handler.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class InvoicePaymentStatusChangedHandler extends AbstractInvoicingHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PaymentDao paymentDao;

    private final CashFlowDao cashFlowDao;

    private final Filter filter;

    @Autowired
    public InvoicePaymentStatusChangedHandler(PaymentDao paymentDao, CashFlowDao cashFlowDao) {
        this.paymentDao = paymentDao;
        this.cashFlowDao = cashFlowDao;
        this.filter = new PathConditionFilter(new PathConditionRule("invoice_payment_change.payload.invoice_payment_status_changed", new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(InvoiceChange invoiceChange, Event event) throws DaoException {
        InvoicePaymentStatus invoicePaymentStatus = invoiceChange.getInvoicePaymentChange().getPayload().getInvoicePaymentStatusChanged().getStatus();
        long eventId = event.getId();
        String invoiceId = event.getSource().getInvoiceId();
        String paymentId = invoiceChange.getInvoicePaymentChange().getId();

        log.info("Start payment status changed handling, eventId={}, invoiceId={}, paymentId={}, status={}",
                eventId, invoiceId, paymentId, invoicePaymentStatus.getSetField().getFieldName());

        com.rbkmoney.newway.domain.tables.pojos.Payment paymentSource = paymentDao.get(invoiceId, paymentId);
        if (paymentSource == null) {
            throw new NotFoundException(String.format("Payment not found, invoiceId='%s', paymentId='%s'", invoiceId, paymentId));
        }
        Long paymentSourceId = paymentSource.getId();
        paymentSource.setId(null);
        paymentSource.setWtime(null);
        paymentSource.setEventId(eventId);
        paymentSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        paymentSource.setStatus(TBaseUtil.unionFieldToEnum(invoicePaymentStatus, PaymentStatus.class));
        if (invoicePaymentStatus.isSetCancelled()) {
            paymentSource.setStatusCancelledReason(invoicePaymentStatus.getCancelled().getReason());
            paymentSource.setStatusCapturedReason(null);
            paymentSource.setStatusFailedFailure(null);
        } else if (invoicePaymentStatus.isSetCaptured()) {
            paymentSource.setStatusCancelledReason(null);
            paymentSource.setStatusCapturedReason(invoicePaymentStatus.getCaptured().getReason());
            paymentSource.setStatusFailedFailure(null);
        } else if (invoicePaymentStatus.isSetFailed()) {
            paymentSource.setStatusCancelledReason(null);
            paymentSource.setStatusCapturedReason(null);
            paymentSource.setStatusFailedFailure(JsonUtil.toJsonString(invoicePaymentStatus.getFailed()));
        }

        paymentDao.updateNotCurrent(invoiceId, paymentId);
        long pmntId = paymentDao.save(paymentSource);
        List<CashFlow> cashFlows = cashFlowDao.getByObjId(paymentSourceId, PaymentChangeType.payment);
        cashFlows.forEach(pcf -> {
            pcf.setId(null);
            pcf.setObjId(pmntId);
        });
        cashFlowDao.save(cashFlows);

        log.info("Payment status has been saved, eventId={}, invoiceId={}, paymentId={}, status={}",
                eventId, invoiceId, paymentId, invoicePaymentStatus.getSetField().getFieldName());
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
