package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.AbstractAppDaoTests;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceDao;
import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.domain.tables.pojos.InvoiceCart;
import com.rbkmoney.newway.model.InvoiceWrapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class InvoiceBatchServiceTest extends AbstractAppDaoTests {

    @Autowired
    private InvoiceBatchService invoiceBatchService;

    @Autowired
    private InvoiceDao invoiceDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void processTest() {
        List<InvoiceWrapper> invoiceWrappers = IntStream.range(1, 5)
                .mapToObj(x -> new InvoiceWrapper(random(Invoice.class, "id"),
                        randomListOf(3, InvoiceCart.class, "id", "invId")))
                .collect(Collectors.toList());

        String invoiceIdFirst = "invoiceIdFirst";
        String invoiceIdSecond = "invoiceIdSecond";
        invoiceWrappers.get(0).getInvoice().setInvoiceId(invoiceIdFirst);
        invoiceWrappers.get(1).getInvoice().setInvoiceId(invoiceIdFirst);
        invoiceWrappers.get(2).getInvoice().setInvoiceId(invoiceIdSecond);
        invoiceWrappers.get(3).getInvoice().setInvoiceId(invoiceIdSecond);
        invoiceWrappers.forEach(iw -> {
            iw.getInvoice().setCurrent(false);
        });
        invoiceBatchService.process(invoiceWrappers);

        Invoice invoiceFirstGet = invoiceDao.get(invoiceIdFirst);
        assertNotEquals(invoiceWrappers.get(0).getInvoice().getPartyId(), invoiceFirstGet.getPartyId());
        assertEquals(invoiceWrappers.get(1).getInvoice().getPartyId(), invoiceFirstGet.getPartyId());

        Invoice invoiceSecondGet = invoiceDao.get(invoiceIdSecond);
        assertNotEquals(invoiceWrappers.get(2).getInvoice().getShopId(), invoiceSecondGet.getShopId());
        assertEquals(invoiceWrappers.get(3).getInvoice().getShopId(), invoiceSecondGet.getShopId());

        //Duplication check
        invoiceBatchService.process(invoiceWrappers);
        assertEquals(2, jdbcTemplate
                .queryForObject("SELECT count(*) FROM nw.invoice WHERE invoice_id = ? ", new Object[]{invoiceIdFirst},
                        Integer.class).intValue());
        assertEquals(2, jdbcTemplate
                .queryForObject("SELECT count(*) FROM nw.invoice WHERE invoice_id = ? ", new Object[]{invoiceIdSecond},
                        Integer.class).intValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT count(*) FROM nw.invoice_cart where inv_id = ? ",
                new Object[]{invoiceFirstGet.getId()}, Integer.class).intValue());
        assertEquals(24,
                jdbcTemplate.queryForObject("SELECT count(*) FROM nw.invoice_cart ", Integer.class).intValue());
    }
}
