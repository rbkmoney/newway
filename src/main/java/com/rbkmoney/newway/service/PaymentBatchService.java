package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.invoicing.impl.PaymentIdsGeneratorDaoImpl;
import com.rbkmoney.newway.model.InvoicingKey;
import com.rbkmoney.newway.model.PaymentWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentBatchService {

    private final PaymentWrapperService paymentWrapperService;
    private final PaymentIdsGeneratorDaoImpl paymentIdsGeneratorDao;
    private final PaymentSquashService paymentSquashService;

    public void process(List<PaymentWrapper> paymentWrappers) {
        log.info("Start processing of payment batch, size={}", paymentWrappers.size());
        List<Long> ids = paymentIdsGeneratorDao.get(paymentWrappers.size());
        List<PaymentWrapper> squashedPaymentWrappers = paymentSquashService.squash(paymentWrappers, ids);
        log.info("After squash size={}", squashedPaymentWrappers.size());
        paymentWrapperService.save(squashedPaymentWrappers);
        Collection<InvoicingKey> invoicingSwitchIds = squashedPaymentWrappers
                .stream()
                .filter(PaymentWrapper::isShouldInsert)
                .collect(Collectors.groupingBy(PaymentWrapper::getKey)).keySet();
        if (!CollectionUtils.isEmpty(invoicingSwitchIds)) {
            log.info("Switch to current ids: {}", invoicingSwitchIds);
            paymentWrapperService.switchCurrent(invoicingSwitchIds);
        }
        log.info("End processing of payment batch");
    }
}
