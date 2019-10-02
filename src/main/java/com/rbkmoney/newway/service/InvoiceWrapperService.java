package com.rbkmoney.newway.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceCartDao;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceDao;
import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.domain.tables.pojos.InvoiceCart;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.LocalStorage;
import com.rbkmoney.newway.model.InvoiceWrapper;
import com.rbkmoney.newway.model.InvoicingKey;
import com.rbkmoney.newway.model.InvoicingType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceWrapperService {

    private final InvoiceDao invoiceDao;
    private final InvoiceCartDao invoiceCartDao;
    private final Cache<InvoicingKey, InvoiceWrapper> invoiceDataCache;

    public InvoiceWrapper get(String invoiceId, LocalStorage storage) throws DaoException, NotFoundException {
        InvoicingKey key = InvoicingKey.builder().invoiceId(invoiceId).type(InvoicingType.INVOICE).build();
        InvoiceWrapper invoiceWrapper = (InvoiceWrapper) storage.getCopy(key);
        if (invoiceWrapper != null) {
            return invoiceWrapper;
        }
        invoiceWrapper = invoiceDataCache.getIfPresent(key);
        if (invoiceWrapper != null) {
            return invoiceWrapper.copy();
        }
        Invoice invoice = invoiceDao.get(invoiceId);
        if (invoice == null) {
            throw new NotFoundException(String.format("Invoice not found, invoiceId='%s'", invoiceId));
        }
        List<InvoiceCart> carts = invoiceCartDao.getByInvId(invoice.getId());
        return new InvoiceWrapper(invoice, carts);
    }

    public void save(List<InvoiceWrapper> invoiceWrappers) {
        invoiceWrappers.forEach(i -> invoiceDataCache.put(InvoicingKey.builder().invoiceId(i.getInvoice().getInvoiceId()).type(InvoicingType.INVOICE).build(), i));
        List<Invoice> invoices = invoiceWrappers.stream().map(InvoiceWrapper::getInvoice).collect(Collectors.toList());
        List<InvoiceCart> carts = invoiceWrappers.stream().map(InvoiceWrapper::getCarts).flatMap(Collection::stream).collect(Collectors.toList());
        invoiceDao.saveBatch(invoices);
        invoiceCartDao.save(carts);
    }
}
