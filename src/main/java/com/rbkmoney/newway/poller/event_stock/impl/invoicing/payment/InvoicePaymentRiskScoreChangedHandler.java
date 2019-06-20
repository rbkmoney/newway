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
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.service.CashFlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentRiskScoreChangedHandler extends AbstractInvoicingHandler {

    private final PaymentDao paymentDao;
    private final CashFlowService cashFlowService;

    private Filter filter = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change.payload.invoice_payment_risk_score_changed",
            new IsNullCondition().not()));;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(InvoiceChange change, MachineEvent event, Integer changeId) {
        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        String invoiceId = event.getSourceId();
        String paymentId = invoicePaymentChange.getId();
        RiskScore riskScore = invoicePaymentChange.getPayload().getInvoicePaymentRiskScoreChanged().getRiskScore();
        long sequenceId = event.getEventId();

        log.info("Start handling payment risk score change, sequenceId='{}', invoiceId='{}', paymentId='{}'", sequenceId, invoiceId, paymentId);
        Payment paymentSource = paymentDao.get(invoiceId, paymentId);
        if (paymentSource == null) {
            throw new NotFoundException(String.format("Invoice payment not found, invoiceId='%s', paymentId='%s'",
                    invoiceId, paymentId));
        }
        Long paymentSourceId = paymentSource.getId();
        paymentSource.setId(null);
        paymentSource.setWtime(null);
        paymentSource.setChangeId(changeId);
        paymentSource.setSequenceId(sequenceId);
        paymentSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        com.rbkmoney.newway.domain.enums.RiskScore score = TypeUtil.toEnumField(riskScore.name(), com.rbkmoney.newway.domain.enums.RiskScore.class);
        if (score == null) {
            throw new IllegalArgumentException("Illegal risk score: " + riskScore);
        }
        paymentSource.setRiskScore(score);
        Long pmntId = paymentDao.save(paymentSource);
        if (pmntId != null) {
            paymentDao.updateNotCurrent(paymentSourceId);
        }
        cashFlowService.save(paymentSourceId, pmntId, PaymentChangeType.payment);
        log.info("Payment risk score have been saved, sequenceId='{}', invoiceId='{}', paymentId='{}'", sequenceId, invoiceId, paymentId);
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
