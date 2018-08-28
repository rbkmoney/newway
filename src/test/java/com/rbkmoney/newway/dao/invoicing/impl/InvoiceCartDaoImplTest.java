package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceCartDao;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceDao;
import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.domain.tables.pojos.InvoiceCart;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static io.github.benas.randombeans.api.EnhancedRandom.random;

import static org.junit.Assert.*;

public class InvoiceCartDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private InvoiceCartDao invoiceCartDao;

    @Autowired
    private InvoiceDao invoiceDao;

    @Test
    public void test() {
        Invoice invoice = random(Invoice.class);
        invoice.setCurrent(true);
        Long invId = invoiceDao.save(invoice);
        List<InvoiceCart> invoiceCarts = randomListOf(10, InvoiceCart.class);
        invoiceCarts.forEach(ic -> {
            ic.setInvId(invId);
        });
        invoiceCartDao.save(invoiceCarts);
        List<InvoiceCart> byInvId = invoiceCartDao.getByInvId(invId);
        assertEquals(new HashSet(invoiceCarts), new HashSet(byInvId));
    }
}