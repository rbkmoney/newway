package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.invoicing.iface.InvoiceDao;
import com.rbkmoney.newway.dao.invoicing.impl.InvoiceIdsGeneratorDaoImpl;
import com.rbkmoney.newway.model.InvoiceWrapper;
import com.rbkmoney.newway.model.InvoicingSwitchKey;
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

    public void process(List<InvoiceWrapper> invoiceWrappers){
        log.info("Start processing of invoice batch, size={}", invoiceWrappers.size());
        List<Long> ids = invoiceIdsGeneratorDao.get(invoiceWrappers.size());
        setIds(invoiceWrappers, ids);
        invoiceWrapperService.save(invoiceWrappers);
        List<InvoicingSwitchKey> invoicingSwitchIds = invoiceWrappers.stream()
                .map(i -> new InvoicingSwitchKey(i.getInvoice().getInvoiceId(), null, i.getInvoice().getId()))
                .collect(Collectors.groupingBy(InvoicingSwitchKey::getInvoiceId, Collectors.maxBy(Comparator.comparing(InvoicingSwitchKey::getId))))
                .values().stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        log.info("Switch to current ids: {}", invoicingSwitchIds);
        invoiceDao.switchCurrent(invoicingSwitchIds);
        log.info("End processing of invoice batch");
    }

    private void setIds(List<InvoiceWrapper> invoiceWrappers, List<Long> ids) {
        for (int i = 0; i < invoiceWrappers.size(); ++i) {
            InvoiceWrapper invoiceWrapper = invoiceWrappers.get(i);
            Long invId = ids.get(i);
            invoiceWrapper.getInvoice().setId(invId);
            invoiceWrapper.getCarts().forEach(c -> c.setInvId(invId));
        }
    }
}
