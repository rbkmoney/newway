package com.rbkmoney.newway.poller.handler.impl.invoicing.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.domain.ContactInfo;
import com.rbkmoney.damsel.domain.CustomerPayer;
import com.rbkmoney.damsel.domain.InvoicePayment;
import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentStarted;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceDao;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.domain.enums.*;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.handler.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.util.CashFlowUtil;
import com.rbkmoney.newway.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class InvoicePaymentCreatedHandler extends AbstractInvoicingHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final InvoiceDao invoiceDao;

    private final PaymentDao paymentDao;

    private final CashFlowDao cashFlowDao;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Filter filter;

    @Autowired
    public InvoicePaymentCreatedHandler(InvoiceDao invoiceDao, PaymentDao paymentDao, CashFlowDao cashFlowDao) {
        this.invoiceDao = invoiceDao;
        this.paymentDao = paymentDao;
        this.cashFlowDao = cashFlowDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "invoice_payment_change.payload.invoice_payment_started",
                new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(InvoiceChange invoiceChange, Event event) {
        InvoicePaymentStarted invoicePaymentStarted = invoiceChange
                .getInvoicePaymentChange()
                .getPayload()
                .getInvoicePaymentStarted();

        Payment payment = new Payment();
        InvoicePayment invoicePayment = invoicePaymentStarted.getPayment();

        log.info("Start payment created handling, eventId={}, invoiceId={}, paymentId={}",
                event.getId(), event.getSource().getInvoiceId(), invoicePayment.getId());

        payment.setEventId(event.getId());
        payment.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        payment.setPaymentId(invoicePayment.getId());
        payment.setCreatedAt(TypeUtil.stringToLocalDateTime(invoicePayment.getCreatedAt()));
        String invoiceId = event.getSource().getInvoiceId();
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
        PaymentStatus status = TypeUtil.toEnumField(invoicePayment.getStatus().getSetField().getFieldName(), PaymentStatus.class);
        if (status == null) {
            throw new IllegalArgumentException("Illegal payment status: " + invoicePayment.getStatus());
        }
        payment.setStatus(status);
        if (invoicePayment.getStatus().isSetCancelled()) {
            payment.setStatusCancelledReason(invoicePayment.getStatus().getCancelled().getReason());
        } else if (invoicePayment.getStatus().isSetCaptured()) {
            payment.setStatusCapturedReason(invoicePayment.getStatus().getCaptured().getReason());
        } else if (invoicePayment.getStatus().isSetFailed()) {
            payment.setStatusFailedFailure(JsonUtil.toJsonString(invoicePayment.getStatus().getFailed()));
        }
        payment.setAmount(invoicePayment.getCost().getAmount());
        payment.setCurrencyCode(invoicePayment.getCost().getCurrency().getSymbolicCode());
        PayerType payerType = TypeUtil.toEnumField(invoicePayment.getPayer().getSetField().getFieldName(), PayerType.class);
        if (payerType == null) {
            throw new IllegalArgumentException("Illegal payer type: " + invoicePayment.getPayer());
        }
        payment.setPayerType(payerType);
        if (invoicePayment.getPayer().isSetPaymentResource()) {
            PaymentTool paymentTool = invoicePayment.getPayer().getPaymentResource().getResource().getPaymentTool();
            fillPaymentTool(payment, paymentTool);
            ContactInfo contactInfo = invoicePayment.getPayer().getPaymentResource().getContactInfo();
            fillContactInfo(payment, contactInfo);
        } else if (invoicePayment.getPayer().isSetCustomer()) {
            CustomerPayer customer = invoicePayment.getPayer().getCustomer();
            payment.setPayerCustomerId(customer.getCustomerId());
            payment.setPayerCustomerBindingId(customer.getCustomerBindingId());
            payment.setPayerCustomerRecPaymentToolId(customer.getRecPaymentToolId());
            fillPaymentTool(payment, customer.getPaymentTool());
            fillContactInfo(payment, customer.getContactInfo());
        }
        PaymentFlowType paymentFlowType = TypeUtil.toEnumField(invoicePayment.getFlow().getSetField().getFieldName(), PaymentFlowType.class);
        if (paymentFlowType == null) {
            throw new IllegalArgumentException("Illegal payment flow type: " + invoicePayment.getPayer());
        }
        payment.setPaymentFlowType(paymentFlowType);
        if (invoicePayment.getFlow().isSetHold()) {
            payment.setPaymentFlowHeldUntil(TypeUtil.stringToLocalDateTime(invoicePayment.getFlow().getHold().getHeldUntil()));
            payment.setPaymentFlowOnHoldExpiration(invoicePayment.getFlow().getHold().getOnHoldExpiration().name());
        }
        if (invoicePaymentStarted.isSetRoute()) {
            payment.setRouteProviderId(invoicePaymentStarted.getRoute().getProvider().getId());
            payment.setRouteTerminalId(invoicePaymentStarted.getRoute().getTerminal().getId());
        }
        long pmntId = paymentDao.save(payment);

        if (invoicePaymentStarted.isSetCashFlow()) {
            List<CashFlow> cashFlowList = CashFlowUtil.convertCashFlows(invoicePaymentStarted.getCashFlow(), pmntId, PaymentChangeType.payment);
            cashFlowDao.save(cashFlowList);
        }

        log.info("Payment has been saved, eventId={}, invoiceId={}, paymentId={}", event.getId(), invoiceId, invoicePayment.getId());
    }

    private void fillContactInfo(Payment payment, ContactInfo contactInfo) {
        payment.setPayerPhoneNumber(contactInfo.getPhoneNumber());
        payment.setPayerEmail(contactInfo.getEmail());
    }

    private void fillPaymentTool(Payment payment, PaymentTool paymentTool) {
        payment.setPayerPaymentToolType(TypeUtil.toEnumField(paymentTool.getSetField().getFieldName(), PaymentToolType.class));
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
        }
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
