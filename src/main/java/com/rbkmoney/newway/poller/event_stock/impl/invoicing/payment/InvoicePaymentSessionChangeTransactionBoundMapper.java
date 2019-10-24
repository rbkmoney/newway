package com.rbkmoney.newway.poller.event_stock.impl.invoicing.payment;

import com.rbkmoney.damsel.domain.AdditionalTransactionInfo;
import com.rbkmoney.damsel.domain.TransactionInfo;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentSessionChange;
import com.rbkmoney.damsel.payment_processing.SessionChangePayload;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.poller.event_stock.*;
import com.rbkmoney.newway.model.PaymentWrapper;
import com.rbkmoney.newway.service.PaymentWrapperService;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentSessionChangeTransactionBoundMapper extends AbstractInvoicingPaymentMapper {

    private final PaymentWrapperService paymentWrapperService;

    private Filter filter = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change.payload.invoice_payment_session_change.payload.session_transaction_bound",
            new IsNullCondition().not()));

    @Override
    public PaymentWrapper map(InvoiceChange change, MachineEvent event, Integer changeId, LocalStorage storage) {
        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        String invoiceId = event.getSourceId();
        String paymentId = invoicePaymentChange.getId();
        InvoicePaymentSessionChange sessionChange = invoicePaymentChange.getPayload().getInvoicePaymentSessionChange();
        long sequenceId = event.getEventId();
        log.info("Start mapping session change transaction info, sequenceId='{}', invoiceId='{}', paymentId='{}'", sequenceId, invoiceId, paymentId);

        PaymentWrapper paymentWrapper = paymentWrapperService.get(invoiceId, paymentId, storage);
        Payment paymentSource = paymentWrapper.getPayment();
        setDefaultProperties(paymentSource, sequenceId, changeId, event.getCreatedAt());
        SessionChangePayload payload = sessionChange.getPayload();
        TransactionInfo transactionInfo = payload.getSessionTransactionBound().getTrx();
        paymentSource.setSessionPayloadTransactionBoundTrxId(transactionInfo.getId());
        Map<String, String> extra = transactionInfo.getExtra();
        if (extra.get("PaRes") != null) {
            extra.put("PaRes", null); 
        }
        paymentSource.setSessionPayloadTransactionBoundTrxExtraJson(JsonUtil.objectToJsonString(extra));

        if (transactionInfo.isSetAdditionalInfo()) {
            AdditionalTransactionInfo additionalTransactionInfo = transactionInfo.getAdditionalInfo();
            paymentSource.setTrxAdditionalInfoRrn(additionalTransactionInfo.getRrn());
            paymentSource.setTrxAdditionalInfoApprovalCode(additionalTransactionInfo.getApprovalCode());
            paymentSource.setTrxAdditionalInfoAcsUrl(additionalTransactionInfo.getAcsUrl());
            //paymentSource.setTrxAdditionalInfoPareq(additionalTransactionInfo.getPareq());
            paymentSource.setTrxAdditionalInfoMd(additionalTransactionInfo.getMd());
            paymentSource.setTrxAdditionalInfoTermUrl(additionalTransactionInfo.getTermUrl());
            //paymentSource.setTrxAdditionalInfoPares(additionalTransactionInfo.getPares());
            paymentSource.setTrxAdditionalInfoEci(additionalTransactionInfo.getEci());
            paymentSource.setTrxAdditionalInfoCavv(additionalTransactionInfo.getCavv());
            paymentSource.setTrxAdditionalInfoXid(additionalTransactionInfo.getXid());
            paymentSource.setTrxAdditionalInfoCavvAlgorithm(additionalTransactionInfo.getCavvAlgorithm());

            if (additionalTransactionInfo.isSetThreeDsVerification()) {
                paymentSource.setTrxAdditionalInfoThreeDsVerification(additionalTransactionInfo.getThreeDsVerification().name());
            }
        }
        log.info("Payment session transaction info has been mapped, sequenceId='{}', invoiceId='{}', paymentId='{}'", sequenceId, invoiceId, paymentId);
        return paymentWrapper;
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
