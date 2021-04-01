package com.rbkmoney.newway.handler.event.stock.impl.invoicing.refund;

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
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.dao.invoicing.iface.RefundDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.enums.RefundStatus;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.domain.tables.pojos.Refund;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
import com.rbkmoney.newway.handler.event.stock.impl.invoicing.InvoicingHandler;
import com.rbkmoney.newway.util.CashFlowUtil;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentRefundCreatedHandler implements InvoicingHandler {

    private final RefundDao refundDao;
    private final PaymentDao paymentDao;
    private final CashFlowDao cashFlowDao;
    private final MachineEventCopyFactory<Refund, Integer> machineEventCopyFactory;

    @Getter
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

        Refund refund = machineEventCopyFactory.create(event, sequenceId, changeId, null);

        refund.setDomainRevision(invoicePaymentRefund.getDomainRevision());
        refund.setRefundId(refundId);
        refund.setPaymentId(paymentId);
        refund.setInvoiceId(invoiceId);
        refund.setExternalId(invoicePaymentRefund.getExternalId());

        Payment payment = paymentDao.get(invoiceId, paymentId);
        refund.setPartyId(payment.getPartyId());
        refund.setShopId(payment.getShopId());
        refund.setCreatedAt(TypeUtil.stringToLocalDateTime(invoicePaymentRefund.getCreatedAt()));
        refund.setStatus(TBaseUtil.unionFieldToEnum(invoicePaymentRefund.getStatus(), RefundStatus.class));
        if (invoicePaymentRefund.getStatus().isSetFailed()) {
            refund.setStatusFailedFailure(
                    JsonUtil.thriftBaseToJsonString(invoicePaymentRefund.getStatus().getFailed()));
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

        refundDao.save(refund).ifPresentOrElse(
                id -> {
                    List<CashFlow> cashFlowList = CashFlowUtil
                            .convertCashFlows(invoicePaymentRefundCreated.getCashFlow(), id, PaymentChangeType.refund);
                    cashFlowDao.save(cashFlowList);
                    refundDao.updateCommissions(id);
                    log.info("Refund has been saved, sequenceId={}, invoiceId={}, paymentId={}, refundId={}",
                            sequenceId, invoiceId, paymentId, refundId);
                },
                () -> log.info("Refund bound duplicated, sequenceId={}, invoiceId={}, paymentId={}, refundId={}",
                        sequenceId, invoiceId, paymentId, refundId));
    }

}
