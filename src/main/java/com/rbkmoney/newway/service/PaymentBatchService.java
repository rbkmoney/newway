package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.dao.invoicing.impl.PaymentIdsGeneratorDaoImpl;
import com.rbkmoney.newway.model.InvoicingSwitchKey;
import com.rbkmoney.newway.model.PaymentWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentBatchService {

    private final PaymentDao paymentDao;
    private final PaymentWrapperService paymentWrapperService;
    private final PaymentIdsGeneratorDaoImpl paymentIdsGeneratorDao;

    @AllArgsConstructor
    @EqualsAndHashCode
    private class Pair {
        private String invoiceId;
        private String paymentId;
    }

    public void process(List<PaymentWrapper> paymentWrappers) {
        log.info("Start processing of payment batch, size={}", paymentWrappers.size());
        List<Long> ids = paymentIdsGeneratorDao.get(paymentWrappers.size());
        setIds(paymentWrappers, ids);
        paymentWrapperService.save(paymentWrappers);
        paymentDao.updateCommissions(paymentWrappers.stream().filter(PaymentWrapper::isNeedUpdateCommissions).map(pw -> pw.getPayment().getId()).collect(Collectors.toList()));
        List<InvoicingSwitchKey> invoicingSwitchIds = paymentWrappers.stream()
                .map(p -> new InvoicingSwitchKey(p.getPayment().getInvoiceId(), p.getPayment().getPaymentId(), p.getPayment().getId()))
                .collect(Collectors.groupingBy(isk ->  new Pair(isk.getInvoiceId(), isk.getPaymentId()), Collectors.maxBy(Comparator.comparing(InvoicingSwitchKey::getId))))
                .values().stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        log.info("Switch to current ids: {}", invoicingSwitchIds);
        paymentDao.switchCurrent(invoicingSwitchIds);
        log.info("End processing of payment batch");
    }

    private void setIds(List<PaymentWrapper> paymentWrappers, List<Long> ids) {
        for (int i = 0; i < paymentWrappers.size(); ++i) {
            PaymentWrapper paymentWrapper = paymentWrappers.get(i);
            Long pmntId = ids.get(i);
            paymentWrapper.getPayment().setId(pmntId);
            paymentWrapper.getCashFlows().forEach(c -> c.setObjId(pmntId));
        }
    }
}
