package com.rbkmoney.newway.poller.event_stock.impl.invoicing.payment;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentStarted;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceDao;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.domain.enums.*;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.util.CashFlowUtil;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentCreatedHandler extends AbstractInvoicingHandler {

    private final InvoiceDao invoiceDao;
    private final PaymentDao paymentDao;
    private final CashFlowDao cashFlowDao;

    private Filter filter = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change.payload.invoice_payment_started",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(InvoiceChange invoiceChange, MachineEvent event, Integer changeId) {
        InvoicePaymentStarted invoicePaymentStarted = invoiceChange
                .getInvoicePaymentChange()
                .getPayload()
                .getInvoicePaymentStarted();

        Payment payment = new Payment();
        InvoicePayment invoicePayment = invoicePaymentStarted.getPayment();

        long sequenceId = event.getEventId();
        String invoiceId = event.getSourceId();

        log.info("Start payment created handling, sequenceId={}, invoiceId={}, paymentId={}",
                sequenceId, invoiceId, invoicePayment.getId());

        payment.setChangeId(changeId);
        payment.setSequenceId(sequenceId);
        payment.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        payment.setPaymentId(invoicePayment.getId());
        payment.setCreatedAt(TypeUtil.stringToLocalDateTime(invoicePayment.getCreatedAt()));
        payment.setInvoiceId(invoiceId);

        Invoice invoice = invoiceDao.get(invoiceId);
        if (invoice == null) {
            throw new NotFoundException(String.format("Invoice on payment not found, invoiceId='%s', paymentId='%s'",
                    invoiceId, invoicePayment.getId()));
        }

        payment.setPartyId(invoice.getPartyId());
        payment.setShopId(invoice.getShopId());
        payment.setDomainRevision(invoicePayment.getDomainRevision());
        if (invoicePayment.isSetPartyRevision()) {
            payment.setPartyRevision(invoicePayment.getPartyRevision());
        }
        payment.setStatus(TBaseUtil.unionFieldToEnum(invoicePayment.getStatus(), PaymentStatus.class));
        if (invoicePayment.getStatus().isSetCancelled()) {
            payment.setStatusCancelledReason(invoicePayment.getStatus().getCancelled().getReason());
        } else if (invoicePayment.getStatus().isSetCaptured()) {
            payment.setStatusCapturedReason(invoicePayment.getStatus().getCaptured().getReason());
        } else if (invoicePayment.getStatus().isSetFailed()) {
            payment.setStatusFailedFailure(JsonUtil.tBaseToJsonString(invoicePayment.getStatus().getFailed()));
        }
        payment.setAmount(invoicePayment.getCost().getAmount());
        payment.setCurrencyCode(invoicePayment.getCost().getCurrency().getSymbolicCode());
        Payer payer = invoicePayment.getPayer();
        payment.setPayerType(TBaseUtil.unionFieldToEnum(payer, PayerType.class));
        if (payer.isSetPaymentResource()) {
            PaymentResourcePayer paymentResource = payer.getPaymentResource();
            fillPaymentTool(payment, paymentResource.getResource().getPaymentTool());
            fillContactInfo(payment, paymentResource.getContactInfo());
            if (paymentResource.getResource().isSetClientInfo()) {
                payment.setPayerIpAddress(paymentResource.getResource().getClientInfo().getIpAddress());
                payment.setPayerFingerprint(paymentResource.getResource().getClientInfo().getFingerprint());
            }
        } else if (payer.isSetCustomer()) {
            CustomerPayer customer = payer.getCustomer();
            payment.setPayerCustomerId(customer.getCustomerId());
            payment.setPayerCustomerBindingId(customer.getCustomerBindingId());
            payment.setPayerCustomerRecPaymentToolId(customer.getRecPaymentToolId());
            fillPaymentTool(payment, customer.getPaymentTool());
            fillContactInfo(payment, customer.getContactInfo());
        } else if (payer.isSetRecurrent()) {
            payment.setPayerRecurrentParentInvoiceId(payer.getRecurrent().getRecurrentParent().getInvoiceId());
            payment.setPayerRecurrentParentPaymentId(payer.getRecurrent().getRecurrentParent().getPaymentId());
            fillPaymentTool(payment, payer.getRecurrent().getPaymentTool());
            fillContactInfo(payment, payer.getRecurrent().getContactInfo());
        }
        payment.setPaymentFlowType(TBaseUtil.unionFieldToEnum(invoicePayment.getFlow(), PaymentFlowType.class));
        if (invoicePayment.getFlow().isSetHold()) {
            payment.setPaymentFlowHeldUntil(TypeUtil.stringToLocalDateTime(invoicePayment.getFlow().getHold().getHeldUntil()));
            payment.setPaymentFlowOnHoldExpiration(invoicePayment.getFlow().getHold().getOnHoldExpiration().name());
        }
        if (invoicePaymentStarted.isSetRoute()) {
            payment.setRouteProviderId(invoicePaymentStarted.getRoute().getProvider().getId());
            payment.setRouteTerminalId(invoicePaymentStarted.getRoute().getTerminal().getId());
        }
        if (invoicePayment.isSetMakeRecurrent()) {
            payment.setMakeRecurrent(invoicePayment.isMakeRecurrent());
        }

        Long pmntId = paymentDao.save(payment);

        if (invoicePaymentStarted.isSetCashFlow()) {
            if (pmntId != null) {
                List<CashFlow> cashFlowList = CashFlowUtil.convertCashFlows(invoicePaymentStarted.getCashFlow(), pmntId, PaymentChangeType.payment);
                cashFlowDao.save(cashFlowList);
                if (!invoicePaymentStarted.getCashFlow().isEmpty()) {
                    paymentDao.updateCommissions(pmntId);
                }
            }
        }

        log.info("Payment has been saved, sequenceId={}, invoiceId={}, paymentId={}", sequenceId, invoiceId, invoicePayment.getId());
    }

    private void fillContactInfo(Payment payment, ContactInfo contactInfo) {
        payment.setPayerPhoneNumber(contactInfo.getPhoneNumber());
        payment.setPayerEmail(contactInfo.getEmail());
    }

    private void fillPaymentTool(Payment payment, PaymentTool paymentTool) {
        payment.setPayerPaymentToolType(TBaseUtil.unionFieldToEnum(paymentTool, PaymentToolType.class));
        if (paymentTool.isSetBankCard()) {
            payment.setPayerBankCardToken(paymentTool.getBankCard().getToken());
            payment.setPayerBankCardPaymentSystem(paymentTool.getBankCard().getPaymentSystem().name());
            payment.setPayerBankCardBin(paymentTool.getBankCard().getBin());
            payment.setPayerBankCardMaskedPan(paymentTool.getBankCard().getMaskedPan());
            if (paymentTool.getBankCard().isSetTokenProvider()) {
                payment.setPayerBankCardTokenProvider(paymentTool.getBankCard().getTokenProvider().name());
            }
        } else if (paymentTool.isSetPaymentTerminal()) {
            payment.setPayerPaymentTerminalType(paymentTool.getPaymentTerminal().getTerminalType().name());
        } else if (paymentTool.isSetDigitalWallet()) {
            payment.setPayerDigitalWalletId(paymentTool.getDigitalWallet().getId());
            payment.setPayerDigitalWalletProvider(paymentTool.getDigitalWallet().getProvider().name());
        } else if (paymentTool.isSetCryptoCurrency()) {
            payment.setPayerCryptoCurrencyType(paymentTool.getCryptoCurrency().toString());
        }
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
