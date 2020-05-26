package com.rbkmoney.newway.poller.event_stock.impl.invoicing.adjustment;

import com.rbkmoney.damsel.domain.InvoicePaymentAdjustment;
import com.rbkmoney.damsel.domain.InvoicePaymentAdjustmentState;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentAdjustmentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.invoicing.iface.AdjustmentDao;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.domain.enums.AdjustmentCashFlowType;
import com.rbkmoney.newway.domain.enums.AdjustmentStatus;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.enums.PaymentStatus;
import com.rbkmoney.newway.domain.tables.pojos.Adjustment;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.util.CashFlowUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentAdjustmentCreatedHandler extends AbstractInvoicingHandler {

    private final AdjustmentDao adjustmentDao;
    private final PaymentDao paymentDao;
    private final CashFlowDao cashFlowDao;

    private Filter filter = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change.payload.invoice_payment_adjustment_change.payload.invoice_payment_adjustment_created",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(InvoiceChange invoiceChange, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        String invoiceId = event.getSourceId();
        InvoicePaymentChange invoicePaymentChange = invoiceChange.getInvoicePaymentChange();
        String paymentId = invoicePaymentChange.getId();
        InvoicePaymentAdjustmentChange invoicePaymentAdjustmentChange = invoicePaymentChange.getPayload()
                .getInvoicePaymentAdjustmentChange();
        InvoicePaymentAdjustment invoicePaymentAdjustment = invoicePaymentAdjustmentChange
                .getPayload().getInvoicePaymentAdjustmentCreated().getAdjustment();
        String adjustmentId = invoicePaymentAdjustment.getId();

        log.info("Start adjustment created handling, sequenceId={}, invoiceId={}, paymentId={}, adjustmentId={}",
                sequenceId, invoiceId, paymentId, adjustmentId);

        Adjustment adjustment = new Adjustment();
        adjustment.setSequenceId(sequenceId);
        adjustment.setChangeId(changeId);
        adjustment.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        adjustment.setDomainRevision(invoicePaymentAdjustment.getDomainRevision());
        adjustment.setAdjustmentId(adjustmentId);
        adjustment.setPaymentId(paymentId);
        adjustment.setInvoiceId(invoiceId);
        Payment payment = paymentDao.get(invoiceId, paymentId);
        if (payment == null) {
            throw new NotFoundException(String.format("Payment on adjustment not found, invoiceId='%s', paymentId='%s', adjustmentId='%s'",
                    invoiceId, paymentId, adjustmentId));
        }
        adjustment.setPartyId(payment.getPartyId());
        adjustment.setShopId(payment.getShopId());
        adjustment.setCreatedAt(TypeUtil.stringToLocalDateTime(invoicePaymentAdjustment.getCreatedAt()));
        adjustment.setStatus(TBaseUtil.unionFieldToEnum(invoicePaymentAdjustment.getStatus(), AdjustmentStatus.class));
        if (invoicePaymentAdjustment.getStatus().isSetCaptured()) {
            adjustment.setStatusCapturedAt(TypeUtil.stringToLocalDateTime(invoicePaymentAdjustment.getStatus().getCaptured().getAt()));
        } else if (invoicePaymentAdjustment.getStatus().isSetCancelled()) {
            adjustment.setStatusCancelledAt(TypeUtil.stringToLocalDateTime(invoicePaymentAdjustment.getStatus().getCancelled().getAt()));
        }
        adjustment.setReason(invoicePaymentAdjustment.getReason());
        if (invoicePaymentAdjustment.isSetPartyRevision()) {
            adjustment.setPartyRevision(invoicePaymentAdjustment.getPartyRevision());
        }
        if (invoicePaymentAdjustment.isSetState()) {
            InvoicePaymentAdjustmentState invoicePaymentAdjustmentState = invoicePaymentAdjustment.getState();
            if (invoicePaymentAdjustmentState.isSetCashFlow()) {
                adjustment.setDomainRevision(invoicePaymentAdjustmentState.getCashFlow().getScenario().getDomainRevision());
            } else if (invoicePaymentAdjustmentState.isSetStatusChange()) {
                PaymentStatus paymentStatus = TBaseUtil.unionFieldToEnum(
                        invoicePaymentAdjustmentState.getStatusChange().getScenario().getTargetStatus(), PaymentStatus.class);
                adjustment.setPaymentStatus(paymentStatus);
            }
        }

        Long oldAmount = CashFlowUtil.computeMerchantAmount(invoicePaymentAdjustment.getOldCashFlowInverse());
        Long newAmount = CashFlowUtil.computeMerchantAmount(invoicePaymentAdjustment.getNewCashFlow());
        long amount = newAmount + oldAmount;
        adjustment.setAmount(amount);

        Long adjId = adjustmentDao.save(adjustment);
        if (adjId != null) {
            List<CashFlow> newCashFlowList = CashFlowUtil.convertCashFlows(
                    invoicePaymentAdjustment.getNewCashFlow(),
                    adjId,
                    PaymentChangeType.adjustment,
                    AdjustmentCashFlowType.new_cash_flow);
            cashFlowDao.save(newCashFlowList);
            List<CashFlow> oldCashFlowList = CashFlowUtil.convertCashFlows(
                    invoicePaymentAdjustment.getOldCashFlowInverse(),
                    adjId,
                    PaymentChangeType.adjustment,
                    AdjustmentCashFlowType.old_cash_flow_inverse);
            cashFlowDao.save(oldCashFlowList);
        }

        log.info("Adjustment has been saved, sequenceId={}, invoiceId={}, paymentId={}, adjustmentId={}",
                sequenceId, invoiceId, paymentId, adjustmentId);
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
