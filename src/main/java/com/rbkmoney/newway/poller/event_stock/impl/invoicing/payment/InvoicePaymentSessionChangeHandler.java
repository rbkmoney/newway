package com.rbkmoney.newway.poller.event_stock.impl.invoicing.payment;

import com.rbkmoney.damsel.domain.TransactionInfo;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.enums.SessionChangePayload;
import com.rbkmoney.newway.domain.enums.SessionChangePayloadFinishedResult;
import com.rbkmoney.newway.domain.enums.SessionTargetStatus;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class InvoicePaymentSessionChangeHandler extends AbstractInvoicingHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PaymentDao paymentDao;

    private final CashFlowDao cashFlowDao;

    private final Filter filter;

    @Autowired
    public InvoicePaymentSessionChangeHandler(PaymentDao paymentDao, CashFlowDao cashFlowDao) {
        this.paymentDao = paymentDao;
        this.cashFlowDao = cashFlowDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "invoice_payment_change.payload.invoice_payment_session_change",
                new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(InvoiceChange change, Event event) {
        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        String invoiceId = event.getSource().getInvoiceId();
        String paymentId = invoicePaymentChange.getId();
        InvoicePaymentSessionChange sessionChange = invoicePaymentChange.getPayload().getInvoicePaymentSessionChange();
        log.info("Start handling session change, eventId='{}', invoiceId='{}', paymentId='{}'", event.getId(), invoiceId, paymentId);
        Payment paymentSource = paymentDao.get(invoiceId, paymentId);
        if (paymentSource == null) {
            throw new NotFoundException(String.format("Invoice payment not found, invoiceId='%s', paymentId='%s'",
                    invoiceId, paymentId));
        }
        Long paymentSourceId = paymentSource.getId();
        paymentSource.setId(null);
        paymentSource.setWtime(null);
        paymentSource.setEventId(event.getId());
        paymentSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        paymentSource.setSessionTarget(TBaseUtil.unionFieldToEnum(sessionChange.getTarget(), SessionTargetStatus.class));
        com.rbkmoney.damsel.payment_processing.SessionChangePayload payload = sessionChange.getPayload();
        paymentSource.setSessionPayload(TBaseUtil.unionFieldToEnum(payload, SessionChangePayload.class));
        if (payload.isSetSessionFinished()) {
            SessionResult sessionResult = payload.getSessionFinished().getResult();
            paymentSource.setSessionPayloadFinishedResult(TBaseUtil.unionFieldToEnum(sessionResult, SessionChangePayloadFinishedResult.class));
            if (sessionResult.isSetSucceeded()) {
                paymentSource.setSessionPayloadFinishedResultFailedFailureJson(null);
            } else if (sessionResult.isSetFailed()) {
                paymentSource.setSessionPayloadFinishedResultFailedFailureJson(JsonUtil.tBaseToJsonString(sessionResult.getFailed().getFailure()));
            }
        } else if (payload.isSetSessionSuspended()) {
            paymentSource.setSessionPayloadSuspendedTag(payload.getSessionSuspended().getTag());
        } else if (payload.isSetSessionTransactionBound()) {
            TransactionInfo transactionInfo = payload.getSessionTransactionBound().getTrx();
            paymentSource.setSessionPayloadTransactionBoundTrxId(transactionInfo.getId());
            if (transactionInfo.isSetTimestamp()) {
                paymentSource.setSessionPayloadTransactionBoundTrxTimestamp(TypeUtil.stringToLocalDateTime(transactionInfo.getTimestamp()));
            }
            paymentSource.setSessionPayloadTransactionBoundTrxExtraJson(JsonUtil.objectToJsonString(transactionInfo.getExtra()));
        } else if (payload.isSetSessionProxyStateChanged()) {
            paymentSource.setSessionPayloadProxyStateChangedProxyState(payload.getSessionProxyStateChanged().getProxyState());
        } else if (payload.isSetSessionInteractionRequested()) {
            paymentSource.setSessionPayloadInteractionRequestedInteractionJson(JsonUtil.tBaseToJsonString(payload.getSessionInteractionRequested().getInteraction()));
        }
        paymentDao.updateNotCurrent(invoiceId, paymentId);
        long pmntId = paymentDao.save(paymentSource);
        List<CashFlow> cashFlows = cashFlowDao.getByObjId(paymentSourceId, PaymentChangeType.payment);
        cashFlows.forEach(pcf -> {
            pcf.setId(null);
            pcf.setObjId(pmntId);
        });
        cashFlowDao.save(cashFlows);
        log.info("Payment session have been saved, eventId='{}', invoiceId='{}', paymentId='{}'", event.getId(), invoiceId, paymentId);
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
