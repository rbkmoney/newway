package com.rbkmoney.newway.poller.event_stock.impl.invoicing.chargeback;

import com.rbkmoney.damsel.domain.InvoicePaymentChargeback;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChargebackChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChargebackCreated;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.invoicing.iface.ChargebackDao;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.domain.enums.ChargebackCategory;
import com.rbkmoney.newway.domain.enums.ChargebackStage;
import com.rbkmoney.newway.domain.enums.ChargebackStatus;
import com.rbkmoney.newway.domain.tables.pojos.Chargeback;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentChargebackCreatedHandler extends AbstractInvoicingHandler {

    private static final Filter CONDITION_FILTER = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change.payload.invoice_payment_chargeback_change.payload.invoice_payment_chargeback_created",
            new IsNullCondition().not()));

    private final ChargebackDao chargebackDao;

    private final PaymentDao paymentDao;

    @Override
    public void handle(InvoiceChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        String invoiceId = event.getSourceId();
        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        String paymentId = invoicePaymentChange.getId();

        InvoicePaymentChargebackChange invoicePaymentChargebackChange = change.getInvoicePaymentChange().getPayload()
                .getInvoicePaymentChargebackChange();
        InvoicePaymentChargebackCreated invoicePaymentChargebackCreated = invoicePaymentChargebackChange.getPayload()
                .getInvoicePaymentChargebackCreated();
        InvoicePaymentChargeback invoicePaymentChargeback = invoicePaymentChargebackCreated.getChargeback();

        String chargebackId = invoicePaymentChargeback.getId();
        log.info("Start chargeback created handling, sequenceId={}, invoiceId={}, paymentId={}, chargebackId={}",
                sequenceId, invoiceId, paymentId, chargebackId);

        Chargeback chargeback = new Chargeback();
        chargeback.setChangeId(changeId);
        chargeback.setSequenceId(sequenceId);
        chargeback.setDomainRevision(invoicePaymentChargeback.getDomainRevision());
        chargeback.setPartyRevision(invoicePaymentChargeback.getPartyRevision());
        chargeback.setChargebackId(chargebackId);
        chargeback.setPaymentId(paymentId);
        chargeback.setInvoiceId(invoiceId);
        chargeback.setExternalId(invoicePaymentChargeback.getExternalId());

        Payment payment = paymentDao.get(invoiceId, paymentId);
        if (payment == null) {
            String errMsg = String.format(
                    "Payment on chargeback not found, invoiceId='%s', paymentId='%s', chargebackId='%s'",
                    invoiceId, paymentId, chargebackId);
            throw new NotFoundException(errMsg);
        }

        chargeback.setPartyId(payment.getPartyId());
        chargeback.setShopId(payment.getShopId());
        chargeback.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        chargeback.setCreatedAt(TypeUtil.stringToLocalDateTime(invoicePaymentChargeback.getCreatedAt()));
        chargeback.setPaymentId(payment.getPaymentId());
        chargeback.setInvoiceId(payment.getInvoiceId());
        chargeback.setStatus(TBaseUtil.unionFieldToEnum(invoicePaymentChargeback.getStatus(), ChargebackStatus.class));
        chargeback.setLevyAmount(invoicePaymentChargeback.getLevy().getAmount());
        chargeback.setLevyCurrencyCode(invoicePaymentChargeback.getLevy().getCurrency().getSymbolicCode());
        chargeback.setAmount(invoicePaymentChargeback.getBody().getAmount());
        chargeback.setCurrencyCode(invoicePaymentChargeback.getBody().getCurrency().getSymbolicCode());
        chargeback.setReasonCode(invoicePaymentChargeback.getReason().getCode());
        chargeback.setReasonCategory(
                TBaseUtil.unionFieldToEnum(invoicePaymentChargeback.getReason().getCategory(), ChargebackCategory.class));
        chargeback.setStage(TBaseUtil.unionFieldToEnum(invoicePaymentChargeback.getStage(), ChargebackStage.class));
        if (invoicePaymentChargeback.getContext() != null) {
            chargeback.setContext(invoicePaymentChargeback.getContext().getData());
        }

        Long savedChargebackId = chargebackDao.save(chargeback);

        log.info("Chargeback has been saved, sequenceId={}, invoiceId={}, paymentId={}, chargebackId={}",
                sequenceId, invoiceId, paymentId, savedChargebackId);
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return CONDITION_FILTER;
    }
}
