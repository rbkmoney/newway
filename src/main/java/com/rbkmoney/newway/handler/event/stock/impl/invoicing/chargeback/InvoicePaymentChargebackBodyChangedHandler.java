package com.rbkmoney.newway.handler.event.stock.impl.invoicing.chargeback;

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
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
import com.rbkmoney.newway.handler.event.stock.impl.invoicing.InvoicingHandler;
import com.rbkmoney.newway.service.CashFlowService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentChargebackBodyChangedHandler implements InvoicingHandler {

    private final ChargebackDao chargebackDao;
    private final CashFlowService cashFlowService;
    private final MachineEventCopyFactory<Chargeback, Integer> machineEventCopyFactory;

    @Getter
    private Filter filter = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change" +
                    ".payload.invoice_payment_chargeback_change.payload.invoice_payment_chargeback_body_changed",
            new IsNullCondition().not()));

    @Override
    public void handle(InvoiceChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        String invoiceId = event.getSourceId();
        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        String paymentId = invoicePaymentChange.getId();
        InvoicePaymentChargebackChange invoicePaymentChargebackChange =
                invoicePaymentChange.getPayload().getInvoicePaymentChargebackChange();
        String chargebackId = invoicePaymentChargebackChange.getId();
        log.info("Start chargeback body change handling, sequenceId={}, invoiceId={}, paymentId={}, chargebackId={}",
                sequenceId, invoiceId, paymentId, chargebackId);

        Chargeback chargebackOld = chargebackDao.get(invoiceId, paymentId, chargebackId);
        Chargeback chargebackNew = machineEventCopyFactory.create(event, sequenceId, changeId, chargebackOld, null);

        InvoicePaymentChargebackBodyChanged invoicePaymentChargebackBodyChanged =
                invoicePaymentChargebackChange.getPayload().getInvoicePaymentChargebackBodyChanged();
        chargebackNew.setAmount(invoicePaymentChargebackBodyChanged.getBody().getAmount());
        chargebackNew.setCurrencyCode(invoicePaymentChargebackBodyChanged.getBody().getCurrency().getSymbolicCode());

        chargebackDao.save(chargebackNew).ifPresentOrElse(
                id -> {
                    Long oldId = chargebackOld.getId();
                    chargebackDao.updateNotCurrent(oldId);
                    cashFlowService.save(oldId, id, PaymentChangeType.chargeback);
                    log.info("Chargeback body changed have been succeeded, " +
                                    "sequenceId={}, invoiceId={}, paymentId={}, chargebackId={}",
                            sequenceId, invoiceId, paymentId, chargebackId);
                },
                () -> log.info("Chargeback body changed bound duplicated, " +
                                "sequenceId={}, invoiceId={}, paymentId={}, chargebackId={}",
                        sequenceId, invoiceId, paymentId, chargebackId));
    }

}
