package com.rbkmoney.newway.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.rbkmoney.newway.dao.invoicing.iface.BatchDao;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.model.Wrapper;
import com.rbkmoney.newway.poller.event_stock.LocalStorage;
import com.rbkmoney.newway.model.InvoicingKey;
import com.rbkmoney.newway.model.PaymentWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentWrapperService {

    private final BatchDao<Payment> paymentDao;
    private final CashFlowDao cashFlowDao;
    private final Cache<InvoicingKey, PaymentWrapper> paymentDataCache;

    public PaymentWrapper get(String invoiceId, String paymentId,
                              long sequenceId, Integer changeId,
                              LocalStorage storage) throws DaoException, NotFoundException {
        InvoicingKey key = InvoicingKey.buildKey(invoiceId, paymentId);
        PaymentWrapper paymentWrapper = (PaymentWrapper) storage.get(key);
        if (paymentWrapper != null) {
            paymentWrapper =  paymentWrapper.copy();
        } else {
            paymentWrapper = paymentDataCache.getIfPresent(key);
            if (paymentWrapper != null) {
                paymentWrapper = paymentWrapper.copy();
            } else {
                Payment payment = paymentDao.get(key);
                if (payment == null) {
                    throw new NotFoundException(String.format("Payment not found, invoiceId='%s', payment='%s'", invoiceId, paymentId));
                }
                List<CashFlow> cashFlows = cashFlowDao.getByObjId(payment.getId(), PaymentChangeType.payment);
                paymentWrapper = new PaymentWrapper(payment, cashFlows);
                paymentWrapper.setKey(key);
            }
        }
        if ((paymentWrapper.getPayment().getSequenceId() > sequenceId) ||
                (paymentWrapper.getPayment().getSequenceId() == sequenceId &&
                        paymentWrapper.getPayment().getChangeId() >= changeId)) {
            paymentWrapper = null;
        }
        return paymentWrapper;
    }

    public void save(List<PaymentWrapper> paymentWrappers) {
        paymentWrappers.forEach(pw -> paymentDataCache.put(pw.getKey(), pw));
        List<Payment> paymentsForInsert = paymentWrappers.stream()
                .filter(Wrapper::isShouldInsert)
                .map(PaymentWrapper::getPayment)
                .collect(Collectors.toList());
        List<Payment> paymentsForUpdate = paymentWrappers.stream()
                .filter(pw -> !pw.isShouldInsert())
                .map(PaymentWrapper::getPayment)
                .collect(Collectors.toList());
        List<CashFlow> cashFlows = paymentWrappers
                .stream()
                .filter(Wrapper::isShouldInsert)
                .filter(p -> p.getCashFlows() != null)
                .map(PaymentWrapper::getCashFlows)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        paymentDao.updateBatch(paymentsForUpdate);
        paymentDao.saveBatch(paymentsForInsert);
        cashFlowDao.save(cashFlows);
    }

    public void switchCurrent(Collection<InvoicingKey> switchIds) {
        paymentDao.switchCurrent(switchIds);
    }
}
