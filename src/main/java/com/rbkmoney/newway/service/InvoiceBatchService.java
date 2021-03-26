package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.invoicing.iface.InvoiceDao;
import com.rbkmoney.newway.dao.invoicing.impl.InvoiceIdsGeneratorDaoImpl;
import com.rbkmoney.newway.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvoiceBatchService {

    private final InvoiceDao invoiceDao;
    private final InvoiceWrapperService invoiceWrapperService;
    private final InvoiceIdsGeneratorDaoImpl invoiceIdsGeneratorDao;

    public void process(List<InvoiceWrapper> invoiceWrappers) {
        log.info("Start processing of invoice batch, size={}", invoiceWrappers.size());
        List<Long> ids = invoiceIdsGeneratorDao.get(invoiceWrappers.size());
        setIds(invoiceWrappers, ids);
        invoiceWrapperService.save(invoiceWrappers);
        Collection<InvoicingKey> invoicingSwitchIds = invoiceWrappers.stream().collect(
                Collectors
                        .groupingBy(i -> new InvoicingKey(i.getInvoice().getInvoiceId(), null, InvoicingType.INVOICE)))
                .keySet();
        log.info("Switch to current ids: {}", invoicingSwitchIds);
        invoiceDao.switchCurrent(invoicingSwitchIds);
        log.info("End processing of invoice batch");
    }

    private void setIds(List<InvoiceWrapper> invoiceWrappers, List<Long> ids) {
        for (int i = 0; i < invoiceWrappers.size(); ++i) {
            InvoiceWrapper invoiceWrapper = invoiceWrappers.get(i);
            Long invId = ids.get(i);
            invoiceWrapper.getInvoice().setId(invId);
            if (invoiceWrapper.getCarts() != null) {
                invoiceWrapper.getCarts().forEach(c -> {
                    c.setId(null);
                    c.setInvId(invId);
                });
            }
        }
    }
}