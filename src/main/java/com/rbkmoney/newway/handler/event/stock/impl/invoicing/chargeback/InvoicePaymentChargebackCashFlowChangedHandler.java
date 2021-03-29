package com.rbkmoney.newway.handler.event.stock.impl.invoicing.chargeback;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChargebackCashFlowChanged;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChargebackChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.dao.invoicing.iface.ChargebackDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Chargeback;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
import com.rbkmoney.newway.handler.event.stock.impl.invoicing.InvoicingHandler;
import com.rbkmoney.newway.service.CashFlowService;
import com.rbkmoney.newway.util.CashFlowUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentChargebackCashFlowChangedHandler implements InvoicingHandler {

    private final ChargebackDao chargebackDao;
    private final CashFlowService cashFlowService;
    private final CashFlowDao cashFlowDao;
    private final MachineEventCopyFactory<Chargeback, Integer> machineEventCopyFactory;

    @Getter
    private Filter filter = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change" +
                    ".payload.invoice_payment_chargeback_change.payload.invoice_payment_chargeback_cash_flow_changed",
            new IsNullCondition().not()));

    @Override
    public void handle(InvoiceChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        String invoiceId = event.getSourceId();
        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        String paymentId = invoicePaymentChange.getId();
        InvoicePaymentChargebackChange invoicePaymentChargebackChange =
                invoicePaymentChange.getPayload().getInvoicePaymentChargebackChange();
        InvoicePaymentChargebackCashFlowChanged invoicePaymentChargebackCashFlowChanged =
                invoicePaymentChargebackChange.getPayload().getInvoicePaymentChargebackCashFlowChanged();
        String chargebackId = invoicePaymentChargebackChange.getId();

        log.info("Start chargeback cash flow changed handling, " +
                        "sequenceId={}, invoiceId={}, paymentId={}, chargebackId={}",
                sequenceId, invoiceId, paymentId, chargebackId);

        Chargeback chargebackOld = chargebackDao.get(invoiceId, paymentId, chargebackId);
        if (chargebackOld == null) {
            throw new NotFoundException(
                    String.format("Chargeback not found, invoiceId='%s', paymentId='%s', chargebackId='%s'",
                            invoiceId, paymentId, chargebackId));
        }
        Chargeback chargebackNew = machineEventCopyFactory.create(event, sequenceId, changeId, chargebackOld, null);

        chargebackDao.save(chargebackNew).ifPresentOrElse(
                id -> {
                    Long oldId = chargebackOld.getId();
                    chargebackDao.updateNotCurrent(oldId);
                    cashFlowService.save(oldId, id, PaymentChangeType.chargeback);
                    List<CashFlow> cashFlows = CashFlowUtil.convertCashFlows(
                            invoicePaymentChargebackCashFlowChanged.getCashFlow(),
                            id,
                            PaymentChangeType.chargeback);
                    cashFlowDao.save(cashFlows);
                    log.info("Chargeback cash flow changed have been succeeded, " +
                                    "sequenceId={}, invoiceId={}, paymentId={}, chargebackId={}",
                            sequenceId, invoiceId, paymentId, chargebackId);
                },
                () -> log.info("Chargeback cash flow changed bound duplicated, " +
                                "sequenceId={}, invoiceId={}, paymentId={}, chargebackId={}",
                        sequenceId, invoiceId, paymentId, chargebackId));
    }
}
