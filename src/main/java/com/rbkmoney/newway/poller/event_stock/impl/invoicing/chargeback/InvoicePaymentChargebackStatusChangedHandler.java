package com.rbkmoney.newway.poller.event_stock.impl.invoicing.chargeback;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChargebackChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChargebackStatusChanged;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.invoicing.iface.ChargebackDao;
import com.rbkmoney.newway.domain.enums.ChargebackStatus;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.Chargeback;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.service.CashFlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentChargebackStatusChangedHandler extends AbstractInvoicingHandler {

    private static final Filter CONDITION_FILTER = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change.payload.invoice_payment_chargeback_change.payload.invoice_payment_chargeback_status_changed",
            new IsNullCondition().not()));

    private final ChargebackDao chargebackDao;
    private final CashFlowService cashFlowService;

    @Override
    public void handle(InvoiceChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        String invoiceId = event.getSourceId();
        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        String paymentId = invoicePaymentChange.getId();
        InvoicePaymentChargebackChange invoicePaymentChargebackChange =
                invoicePaymentChange.getPayload().getInvoicePaymentChargebackChange();
        InvoicePaymentChargebackStatusChanged invoicePaymentChargebackStatusChanged =
                invoicePaymentChargebackChange.getPayload().getInvoicePaymentChargebackStatusChanged();
        String chargebackId= invoicePaymentChargebackChange.getId();

        log.info("Start chargeback status changed handling, sequenceId={}, invoiceId={}, paymentId={}, chargebackId={}, status={}",
                sequenceId, invoiceId, paymentId, chargebackId, invoicePaymentChargebackStatusChanged.getStatus().getSetField().getFieldName());

        Chargeback chargebackSource = chargebackDao.get(invoiceId, paymentId, chargebackId);
        if (chargebackSource == null) {
            throw new NotFoundException(String.format("Chargeback not found, invoiceId='%s', paymentId='%s', chargebackId='%s'",
                    invoiceId, paymentId, chargebackId));
        }

        Long chargebackSourceId = chargebackSource.getId();
        chargebackSource.setId(null);
        chargebackSource.setWtime(null);
        chargebackSource.setChangeId(changeId);
        chargebackSource.setSequenceId(sequenceId);
        chargebackSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        chargebackSource.setStatus(TBaseUtil.unionFieldToEnum(invoicePaymentChargebackStatusChanged.getStatus(), ChargebackStatus.class));
        Long savedChargebackId = chargebackDao.save(chargebackSource);
        if (savedChargebackId != null) {
            chargebackDao.updateNotCurrent(chargebackSourceId);
            cashFlowService.save(chargebackSourceId, savedChargebackId, PaymentChangeType.chargeback);
        }
        log.info("Chargeback status changed have been succeeded, sequenceId={}, invoiceId={}, paymentId={}, refundId={}, status={}",
                sequenceId, invoiceId, paymentId, chargebackId, invoicePaymentChargebackStatusChanged.getStatus().getSetField().getFieldName());
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return CONDITION_FILTER;
    }
}
