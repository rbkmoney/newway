package com.rbkmoney.newway.poller.event_stock.impl.invoicing.payment;

import com.rbkmoney.damsel.domain.InvoicePaymentCaptured;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.enums.PaymentStatus;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.service.CashFlowService;
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
public class InvoicePaymentStatusChangedHandler extends AbstractInvoicingHandler {

    private final PaymentDao paymentDao;
    private final CashFlowService cashFlowService;

    private Filter filter = new PathConditionFilter(
            new PathConditionRule("invoice_payment_change.payload.invoice_payment_status_changed", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(InvoiceChange invoiceChange, MachineEvent event, Integer changeId) throws DaoException {
        InvoicePaymentStatus invoicePaymentStatus = invoiceChange.getInvoicePaymentChange().getPayload().getInvoicePaymentStatusChanged().getStatus();
        long sequenceId = event.getEventId();
        String invoiceId = event.getSourceId();
        String paymentId = invoiceChange.getInvoicePaymentChange().getId();

        log.info("Start payment status changed handling, sequenceId={}, invoiceId={}, paymentId={}, status={}",
                sequenceId, invoiceId, paymentId, invoicePaymentStatus.getSetField().getFieldName());

        com.rbkmoney.newway.domain.tables.pojos.Payment paymentSource = paymentDao.get(invoiceId, paymentId);
        if (paymentSource == null) {
            throw new NotFoundException(String.format("Payment not found, invoiceId='%s', paymentId='%s'", invoiceId, paymentId));
        }
        Long paymentSourceId = paymentSource.getId();
        paymentSource.setId(null);
        paymentSource.setWtime(null);
        paymentSource.setChangeId(changeId);
        paymentSource.setSequenceId(sequenceId);
        paymentSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
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

        paymentDao.updateNotCurrent(invoiceId, paymentId);
        long pmntId = paymentDao.save(paymentSource);
        cashFlowService.save(paymentSourceId, pmntId, PaymentChangeType.payment);

        log.info("Payment status has been saved, sequenceId={}, invoiceId={}, paymentId={}, status={}",
                sequenceId, invoiceId, paymentId, invoicePaymentStatus.getSetField().getFieldName());
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
