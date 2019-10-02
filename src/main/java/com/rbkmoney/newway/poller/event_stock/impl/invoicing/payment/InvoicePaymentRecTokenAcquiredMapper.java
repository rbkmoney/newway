package com.rbkmoney.newway.poller.event_stock.impl.invoicing.payment;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.poller.event_stock.*;
import com.rbkmoney.newway.model.InvoicingKey;
import com.rbkmoney.newway.model.InvoicingType;
import com.rbkmoney.newway.model.PaymentWrapper;
import com.rbkmoney.newway.service.PaymentWrapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentRecTokenAcquiredMapper extends AbstractInvoicingPaymentMapper {

    private final PaymentWrapperService paymentWrapperService;

    private Filter filter = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change.payload.invoice_payment_rec_token_acquired",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PaymentWrapper map(InvoiceChange change, MachineEvent event, Integer changeId, LocalStorage storage) {
        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        String invoiceId = event.getSourceId();
        String paymentId = invoicePaymentChange.getId();
        String token = invoicePaymentChange.getPayload().getInvoicePaymentRecTokenAcquired().getToken();
        long sequenceId = event.getEventId();
        log.info("Start handling payment recurrent token acquired, sequenceId='{}', invoiceId='{}', paymentId='{}'", sequenceId, invoiceId, paymentId);
        PaymentWrapper paymentWrapper = paymentWrapperService.get(invoiceId, paymentId, storage);
        Payment paymentSource = paymentWrapper.getPayment();
        setDefaultProperties(paymentSource, sequenceId, changeId, event.getCreatedAt());
        paymentSource.setRecurrentIntentionToken(token);
        paymentWrapper.getCashFlows().forEach(c -> c.setId(null));
        storage.put(InvoicingKey.builder().invoiceId(invoiceId).paymentId(paymentId).type(InvoicingType.PAYMENT).build(), paymentWrapper);
        log.info("Payment recurrent token have been saved, sequenceId='{}', invoiceId='{}', paymentId='{}'", sequenceId, invoiceId, paymentId);
        return paymentWrapper;
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
