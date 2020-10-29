package com.rbkmoney.newway.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.rbkmoney.newway.dao.invoicing.iface.BatchDao;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceCartDao;
import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.domain.tables.pojos.InvoiceCart;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.model.Wrapper;
import com.rbkmoney.newway.poller.event_stock.LocalStorage;
import com.rbkmoney.newway.model.InvoiceWrapper;
import com.rbkmoney.newway.model.InvoicingKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceWrapperService {

    private final BatchDao<Invoice> invoiceDao;
    private final InvoiceCartDao invoiceCartDao;
    private final Cache<InvoicingKey, InvoiceWrapper> invoiceDataCache;

    public InvoiceWrapper get(String invoiceId, long sequenceId, Integer changeId,
                              LocalStorage storage) throws DaoException, NotFoundException {
        InvoicingKey key = InvoicingKey.buildKey(invoiceId);
        InvoiceWrapper invoiceWrapper = (InvoiceWrapper) storage.get(key);
        if (invoiceWrapper != null) {
            invoiceWrapper = invoiceWrapper.copy();
        } else {
            invoiceWrapper = invoiceDataCache.getIfPresent(key);
            if (invoiceWrapper != null) {
                invoiceWrapper = invoiceWrapper.copy();
            } else {
                Invoice invoice = invoiceDao.get(key);
                if (invoice == null) {
                    throw new NotFoundException(String.format("Invoice not found, invoiceId='%s'", invoiceId));
                }
                List<InvoiceCart> carts = invoiceCartDao.getByInvId(invoice.getId());
                invoiceWrapper = new InvoiceWrapper(invoice, carts);
                invoiceWrapper.setKey(key);
            }
        }
        if ((invoiceWrapper.getInvoice().getSequenceId() > sequenceId) ||
                (invoiceWrapper.getInvoice().getSequenceId() == sequenceId &&
                        invoiceWrapper.getInvoice().getChangeId() >= changeId)) {
            invoiceWrapper = null;
        }
        return invoiceWrapper;
    }

    public void save(List<InvoiceWrapper> invoiceWrappers) {
        invoiceWrappers.forEach(iw -> invoiceDataCache.put(iw.getKey(), iw));
        List<Invoice> invoicesForInsert = invoiceWrappers.stream()
                .filter(Wrapper::isShouldInsert)
                .map(InvoiceWrapper::getInvoice)
                .collect(Collectors.toList());
        List<Invoice> invoicesForUpdate = invoiceWrappers.stream()
                .filter(iw -> !iw.isShouldInsert())
                .map(InvoiceWrapper::getInvoice)
                .collect(Collectors.toList());
        List<InvoiceCart> carts = invoiceWrappers
                .stream()
                .filter(Wrapper::isShouldInsert)
                .filter(i -> i.getCarts() != null)
                .map(InvoiceWrapper::getCarts)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        invoiceDao.updateBatch(invoicesForUpdate);
        invoiceDao.saveBatch(invoicesForInsert);
        invoiceCartDao.save(carts);
    }

    public void switchCurrent(Collection<InvoicingKey> switchIds) {
        invoiceDao.switchCurrent(switchIds);
    }
}
