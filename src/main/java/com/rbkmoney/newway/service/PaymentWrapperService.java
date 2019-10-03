package com.rbkmoney.newway.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.LocalStorage;
import com.rbkmoney.newway.model.InvoicingKey;
import com.rbkmoney.newway.model.InvoicingType;
import com.rbkmoney.newway.model.PaymentWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentWrapperService {

    private final PaymentDao paymentDao;
    private final CashFlowDao cashFlowDao;
    private final Cache<InvoicingKey, PaymentWrapper> paymentDataCache;

    public PaymentWrapper get(String invoiceId, String paymentId, LocalStorage storage) throws DaoException, NotFoundException {
        InvoicingKey key = InvoicingKey.builder().invoiceId(invoiceId).paymentId(paymentId).type(InvoicingType.PAYMENT).build();
        PaymentWrapper paymentWrapper = (PaymentWrapper) storage.get(key);
        if (paymentWrapper != null) {
            return paymentWrapper.copy();
        }
        paymentWrapper = paymentDataCache.getIfPresent(key);
        if (paymentWrapper != null) {
            return paymentWrapper.copy();
        }
        Payment payment = paymentDao.get(invoiceId, paymentId);
        if (payment == null) {
            throw new NotFoundException(String.format("Payment not found, invoiceId='%s', payment='%s'", invoiceId, paymentId));
        }
        List<CashFlow> cashFlows = cashFlowDao.getByObjId(payment.getId(), PaymentChangeType.payment);
        return new PaymentWrapper(payment, cashFlows, false);
    }

    public void save(List<PaymentWrapper> paymentWrappers) {
        paymentWrappers.forEach(i -> paymentDataCache.put(InvoicingKey.builder().invoiceId(i.getPayment().getInvoiceId()).type(InvoicingType.INVOICE).build(), i));
        List<Payment> payments = paymentWrappers.stream().map(PaymentWrapper::getPayment).collect(Collectors.toList());
        List<CashFlow> cashFlows = paymentWrappers.stream().map(PaymentWrapper::getCashFlows).flatMap(Collection::stream).collect(Collectors.toList());
        paymentDao.saveBatch(payments);
        cashFlowDao.save(cashFlows);
    }
}
