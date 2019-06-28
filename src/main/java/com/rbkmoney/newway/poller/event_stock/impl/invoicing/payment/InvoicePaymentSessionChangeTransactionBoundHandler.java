package com.rbkmoney.newway.poller.event_stock.impl.invoicing.payment;

import com.rbkmoney.damsel.domain.AdditionalTransactionInfo;
import com.rbkmoney.damsel.domain.TransactionInfo;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentSessionChange;
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
public class InvoicePaymentSessionChangeTransactionBoundHandler extends AbstractInvoicingHandler {

    private final PaymentDao paymentDao;
    private final CashFlowService cashFlowService;

    private Filter filter = new PathConditionFilter(new PathConditionRule(
            "invoice_payment_change.payload.invoice_payment_session_change.payload.session_transaction_bound",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(InvoiceChange change, MachineEvent event, Integer changeId) {
        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        String invoiceId = event.getSourceId();
        String paymentId = invoicePaymentChange.getId();
        InvoicePaymentSessionChange sessionChange = invoicePaymentChange.getPayload().getInvoicePaymentSessionChange();
        long sequenceId = event.getEventId();

        log.info("Start handling session change transaction info, sequenceId='{}', invoiceId='{}', paymentId='{}'", sequenceId, invoiceId, paymentId);
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
        com.rbkmoney.damsel.payment_processing.SessionChangePayload payload = sessionChange.getPayload();
        TransactionInfo transactionInfo = payload.getSessionTransactionBound().getTrx();
        paymentSource.setSessionPayloadTransactionBoundTrxId(transactionInfo.getId());
        //paymentSource.setSessionPayloadTransactionBoundTrxExtraJson(JsonUtil.objectToJsonString(transactionInfo.getExtra()));

        if (transactionInfo.isSetAdditionalInfo()) {
            AdditionalTransactionInfo additionalTransactionInfo = transactionInfo.getAdditionalInfo();
            paymentSource.setTrxAdditionalInfoRrn(additionalTransactionInfo.getRrn());
            paymentSource.setTrxAdditionalInfoApprovalCode(additionalTransactionInfo.getApprovalCode());
            paymentSource.setTrxAdditionalInfoAcsUrl(additionalTransactionInfo.getAcsUrl());
            paymentSource.setTrxAdditionalInfoPareq(additionalTransactionInfo.getPareq());
            paymentSource.setTrxAdditionalInfoMd(additionalTransactionInfo.getMd());
            paymentSource.setTrxAdditionalInfoTermUrl(additionalTransactionInfo.getTermUrl());
            paymentSource.setTrxAdditionalInfoPares(additionalTransactionInfo.getPares());
            paymentSource.setTrxAdditionalInfoEci(additionalTransactionInfo.getEci());
            paymentSource.setTrxAdditionalInfoCavv(additionalTransactionInfo.getCavv());
            paymentSource.setTrxAdditionalInfoXid(additionalTransactionInfo.getXid());
            paymentSource.setTrxAdditionalInfoCavvAlgorithm(additionalTransactionInfo.getCavvAlgorithm());

            if(additionalTransactionInfo.isSetThreeDsVerification()) {
                paymentSource.setTrxAdditionalInfoThreeDsVerification(additionalTransactionInfo.getThreeDsVerification().name());
            }
        }

        Long pmntId = paymentDao.save(paymentSource);
        if (pmntId != null) {
            paymentDao.updateNotCurrent(paymentSourceId);
            cashFlowService.save(paymentSourceId, pmntId, PaymentChangeType.payment);
        }
        log.info("Payment session transaction info has been saved, sequenceId='{}', invoiceId='{}', paymentId='{}'", sequenceId, invoiceId, paymentId);
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
