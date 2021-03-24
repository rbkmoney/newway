package com.rbkmoney.newway.poller.event_stock.impl.invoicing.chargeback;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChargebackBodyChanged;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChargebackChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.invoicing.iface.ChargebackDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.Chargeback;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.service.CashFlowService;
import com.rbkmoney.newway.util.CachbackUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentChargebackBodyChangedHandler extends AbstractInvoicingHandler {

    private static final Filter CONDITION_FILTER = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change.payload.invoice_payment_chargeback_change.payload.invoice_payment_chargeback_body_changed",
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
        InvoicePaymentChargebackBodyChanged invoicePaymentChargebackBodyChanged =
                invoicePaymentChargebackChange.getPayload().getInvoicePaymentChargebackBodyChanged();
        String chargebackId= invoicePaymentChargebackChange.getId();

        log.info("Start chargeback body change handling, sequenceId={}, invoiceId={}, paymentId={}, chargebackId={}",
                sequenceId, invoiceId, paymentId, chargebackId);

        Chargeback chargebackSource = chargebackDao.get(invoiceId, paymentId, chargebackId);
        if (chargebackSource == null) {
            throw new NotFoundException(String.format("Chargeback not found, invoiceId='%s', paymentId='%s', chargebackId='%s'",
                    invoiceId, paymentId, chargebackId));
        }

        Long chargebackSourceId = chargebackSource.getId();
        CachbackUtil.resetBaseFields(chargebackSource, event, changeId, sequenceId);
        chargebackSource.setAmount(invoicePaymentChargebackBodyChanged.getBody().getAmount());
        chargebackSource.setCurrencyCode(invoicePaymentChargebackBodyChanged.getBody().getCurrency().getSymbolicCode());
        Long savedChargebackId = chargebackDao.save(chargebackSource);
        if (savedChargebackId != null) {
            chargebackDao.updateNotCurrent(savedChargebackId);
            cashFlowService.save(chargebackSourceId, savedChargebackId, PaymentChangeType.chargeback);
        }
        log.info("Chargeback body changed have been succeeded, sequenceId={}, invoiceId={}, paymentId={}, chargebackId={}",
                sequenceId, invoiceId, paymentId, chargebackId);
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return CONDITION_FILTER;
    }

}
