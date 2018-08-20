package com.rbkmoney.newway.poller.handler.impl.invoicing.adjustment;

import com.rbkmoney.damsel.domain.InvoicePaymentAdjustmentStatus;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentAdjustmentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.invoicing.iface.AdjustmentDao;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.domain.enums.Adjustmentcashflowtype;
import com.rbkmoney.newway.domain.enums.Adjustmentstatus;
import com.rbkmoney.newway.domain.tables.pojos.Adjustment;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.handler.impl.invoicing.AbstractInvoicingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvoicePaymentAdjustmentStatusChangedHandler extends AbstractInvoicingHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AdjustmentDao adjustmentDao;

    private final CashFlowDao cashFlowDao;

    private final Filter filter;

    @Autowired
    public InvoicePaymentAdjustmentStatusChangedHandler(AdjustmentDao adjustmentDao, CashFlowDao cashFlowDao) {
        this.adjustmentDao = adjustmentDao;
        this.cashFlowDao = cashFlowDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "invoice_payment_change.payload.invoice_payment_adjustment_change.payload.invoice_payment_adjustment_status_changed",
                new IsNullCondition().not()));
    }

    @Override
    public void handle(InvoiceChange invoiceChange, Event event) {
        long eventId = event.getId();
        String invoiceId = event.getSource().getInvoiceId();
        InvoicePaymentChange invoicePaymentChange = invoiceChange.getInvoicePaymentChange();
        String paymentId = invoiceChange.getInvoicePaymentChange().getId();
        InvoicePaymentAdjustmentChange invoicePaymentAdjustmentChange = invoicePaymentChange.getPayload().getInvoicePaymentAdjustmentChange();
        InvoicePaymentAdjustmentStatus invoicePaymentAdjustmentStatus = invoicePaymentAdjustmentChange.getPayload().getInvoicePaymentAdjustmentStatusChanged().getStatus();
        String adjustmentId = invoicePaymentAdjustmentChange.getId();

        log.info("Start adjustment status changed handling, eventId={}, invoiceId={}, paymentId={}, adjustmentId={}, status={}",
                eventId, invoiceId, paymentId, adjustmentId, invoicePaymentAdjustmentStatus.getSetField().getFieldName());
        Adjustment adjustmentSource = adjustmentDao.get(invoiceId, paymentId, adjustmentId);
        if (adjustmentSource == null) {
            throw new NotFoundException(String.format("Adjustment not found, invoiceId='%s', paymentId='%s', adjustmentId='%s'",
                    invoiceId, paymentId, adjustmentId));
        }
        Long adjustmentSourceId = adjustmentSource.getId();
        adjustmentSource.setId(null);
        adjustmentSource.setEventId(eventId);
        adjustmentSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        Adjustmentstatus status = TypeUtil.toEnumField(invoicePaymentAdjustmentStatus.getSetField().getFieldName(), Adjustmentstatus.class);
        if (status == null) {
            throw new IllegalArgumentException("Illegal adjustment status: " + invoicePaymentAdjustmentStatus);
        }
        adjustmentSource.setStatus(status);
        if (invoicePaymentAdjustmentStatus.isSetCaptured()) {
            adjustmentSource.setStatusCapturedAt(TypeUtil.stringToLocalDateTime(invoicePaymentAdjustmentStatus.getCaptured().getAt()));
            adjustmentSource.setStatusCancelledAt(null);
        } else if (invoicePaymentAdjustmentStatus.isSetCancelled()) {
            adjustmentSource.setStatusCapturedAt(null);
            adjustmentSource.setStatusCancelledAt(TypeUtil.stringToLocalDateTime(invoicePaymentAdjustmentStatus.getCancelled().getAt()));
        }
        adjustmentDao.update(invoiceId, paymentId, adjustmentId);
        long adjId = adjustmentDao.save(adjustmentSource);
        List<CashFlow> newCashFlows = cashFlowDao.getForAdjustments(adjustmentSourceId, Adjustmentcashflowtype.new_cash_flow);
        newCashFlows.forEach(pcf -> {
            pcf.setId(null);
            pcf.setObjId(adjId);
        });
        cashFlowDao.save(newCashFlows);
        List<CashFlow> oldCashFlows = cashFlowDao.getForAdjustments(adjustmentSourceId, Adjustmentcashflowtype.old_cash_flow_inverse);
        oldCashFlows.forEach(pcf -> {
            pcf.setId(null);
            pcf.setObjId(adjId);
        });
        cashFlowDao.save(oldCashFlows);

        log.info("Adjustment status change has been saved, eventId={}, invoiceId={}, paymentId={}, adjustmentId={}, status={}",
                eventId, invoiceId, paymentId, adjustmentId, invoicePaymentAdjustmentStatus.getSetField().getFieldName());
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
