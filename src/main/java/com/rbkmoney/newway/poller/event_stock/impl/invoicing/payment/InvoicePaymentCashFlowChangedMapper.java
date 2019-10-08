package com.rbkmoney.newway.poller.event_stock.impl.invoicing.payment;

import com.rbkmoney.damsel.domain.FinalCashFlowPosting;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.poller.event_stock.*;
import com.rbkmoney.newway.model.PaymentWrapper;
import com.rbkmoney.newway.service.PaymentWrapperService;
import com.rbkmoney.newway.util.CashFlowType;
import com.rbkmoney.newway.util.CashFlowUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentCashFlowChangedMapper extends AbstractInvoicingPaymentMapper {

    private final PaymentWrapperService paymentWrapperService;

    private Filter filter = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change.payload.invoice_payment_cash_flow_changed",
            new IsNullCondition().not()));

    @Override
    public PaymentWrapper map(InvoiceChange change, MachineEvent event, Integer changeId, LocalStorage storage) {
        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        String invoiceId = event.getSourceId();
        String paymentId = invoicePaymentChange.getId();
        long sequenceId = event.getEventId();
        log.info("Start mapping payment cashflow change, sequenceId='{}', invoiceId='{}', paymentId='{}'", sequenceId, invoiceId, paymentId);
        PaymentWrapper paymentWrapper = paymentWrapperService.get(invoiceId, paymentId, storage);
        Payment paymentSource = paymentWrapper.getPayment();
        setDefaultProperties(paymentSource, sequenceId, changeId, event.getCreatedAt());
        List<FinalCashFlowPosting> finalCashFlow = invoicePaymentChange.getPayload().getInvoicePaymentCashFlowChanged().getCashFlow();
        List<CashFlow> cashFlows = CashFlowUtil.convertCashFlows(finalCashFlow, null, PaymentChangeType.payment);
        paymentWrapper.setCashFlows(cashFlows);
        Map<CashFlowType, Long> parsedCashFlow = CashFlowUtil.parseCashFlow(finalCashFlow);
        paymentSource.setFee(parsedCashFlow.getOrDefault(CashFlowType.FEE, 0L));
        paymentSource.setProviderFee(parsedCashFlow.getOrDefault(CashFlowType.PROVIDER_FEE, 0L));
        paymentSource.setExternalFee(parsedCashFlow.getOrDefault(CashFlowType.EXTERNAL_FEE, 0L));
        paymentSource.setGuaranteeDeposit(parsedCashFlow.getOrDefault(CashFlowType.GUARANTEE_DEPOSIT, 0L));
        log.info("Payment cashflow has been mapped, sequenceId='{}', invoiceId='{}', paymentId='{}'", sequenceId, invoiceId, paymentId);
        return paymentWrapper;
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
