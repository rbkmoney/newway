package com.rbkmoney.newway.poller.handler.impl.invoicing.payment;

import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.domain.enums.Paymentchangetype;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.handler.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.util.CashFlowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class InvoicePaymentCashFlowChangedHandler extends AbstractInvoicingHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PaymentDao paymentDao;

    private final CashFlowDao cashFlowDao;

    private final Filter filter;

    public InvoicePaymentCashFlowChangedHandler(PaymentDao paymentDao, CashFlowDao cashFlowDao) {
        this.paymentDao = paymentDao;
        this.cashFlowDao = cashFlowDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "invoice_payment_change.payload.invoice_payment_cash_flow_changed",
                new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(InvoiceChange change, Event event) {
        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        String invoiceId = event.getSource().getInvoiceId();
        String paymentId = invoicePaymentChange.getId();
        log.info("Start handling payemnt cashflow change, eventId='{}', invoiceId='{}', paymentId='{}'", event.getId(), invoiceId, paymentId);
        Payment paymentSource = paymentDao.get(invoiceId, paymentId);
        if (paymentSource == null) {
            throw new NotFoundException(String.format("Payment not found, invoiceId='%s', paymentId='%s'",
                    invoiceId, paymentId));
        }
        paymentSource.setId(null);
        paymentSource.setEventId(event.getId());
        paymentSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        paymentDao.update(invoiceId, paymentId);
        long pmntId = paymentDao.save(paymentSource);
        List<CashFlow> cashFlows = CashFlowUtil.convertCashFlows(invoicePaymentChange.getPayload().getInvoicePaymentCashFlowChanged().getCashFlow(), pmntId, Paymentchangetype.payment);
        cashFlowDao.save(cashFlows);
        log.info("Payment cashflow has been saved, eventId='{}', invoiceId='{}', paymentId='{}'", event.getId(), invoiceId, paymentId);
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
