package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.invoicing.iface.InvoiceDao;
import com.rbkmoney.newway.dao.invoicing.impl.InvoiceIdsGeneratorDaoImpl;
import com.rbkmoney.newway.model.InvoiceWrapper;
import com.rbkmoney.newway.model.InvoicingKey;
import com.rbkmoney.newway.model.InvoicingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvoiceBatchService {

    private final InvoiceDao invoiceDao;
    private final InvoiceWrapperService invoiceWrapperService;
    private final InvoiceIdsGeneratorDaoImpl invoiceIdsGeneratorDao;
    private final InvoiceSquashService invoiceSquashService;

    public void process(List<InvoiceWrapper> invoiceWrappers){
        log.info("Start processing of invoice batch, size={}", invoiceWrappers.size());
        List<Long> ids = invoiceIdsGeneratorDao.get(invoiceWrappers.size());
        List<InvoiceWrapper> squashedInvoiceWrappers = invoiceSquashService.squashPayments(invoiceWrappers, ids);
        invoiceWrapperService.save(squashedInvoiceWrappers);
        Collection<InvoicingKey> invoicingSwitchIds = squashedInvoiceWrappers
                .stream()
                .filter(InvoiceWrapper::isShouldInsert)
                .collect(Collectors.groupingBy(InvoiceWrapper::getKey)).keySet();
        log.info("Switch to current ids: {}", invoicingSwitchIds);
        invoiceDao.switchCurrent(invoicingSwitchIds);
        log.info("End processing of invoice batch");
    }
}
