package com.rbkmoney.newway.poller.event_stock.impl.invoicing.payment;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.poller.event_stock.*;
import com.rbkmoney.newway.model.PaymentWrapper;
import com.rbkmoney.newway.service.PaymentWrapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentRiskScoreChangedMapper extends AbstractInvoicingPaymentMapper {

    private final PaymentWrapperService paymentWrapperService;

    private Filter filter = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change.payload.invoice_payment_risk_score_changed",
            new IsNullCondition().not()));;

    @Override
    public PaymentWrapper map(InvoiceChange change, MachineEvent event, Integer changeId, LocalStorage storage) {
        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        String invoiceId = event.getSourceId();
        String paymentId = invoicePaymentChange.getId();
        RiskScore riskScore = invoicePaymentChange.getPayload().getInvoicePaymentRiskScoreChanged().getRiskScore();
        long sequenceId = event.getEventId();
        log.info("Start mapping payment risk score change, sequenceId='{}', invoiceId='{}', paymentId='{}'", sequenceId, invoiceId, paymentId);
        PaymentWrapper paymentWrapper = paymentWrapperService.get(invoiceId, paymentId, sequenceId, changeId, storage);
        if (paymentWrapper == null) {
            return null;
        }
        Payment paymentSource = paymentWrapper.getPayment();
        setDefaultProperties(paymentSource, sequenceId, changeId, event.getCreatedAt());
        com.rbkmoney.newway.domain.enums.RiskScore score = TypeUtil.toEnumField(riskScore.name(), com.rbkmoney.newway.domain.enums.RiskScore.class);
        if (score == null) {
            throw new IllegalArgumentException("Illegal risk score: " + riskScore);
        }
        paymentSource.setRiskScore(score);
        log.info("Payment risk score have been mapped, sequenceId='{}', invoiceId='{}', paymentId='{}'", sequenceId, invoiceId, paymentId);
        return paymentWrapper;
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
