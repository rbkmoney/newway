package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.invoicing.iface.InvoiceDao;
import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.exception.DaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InvoicingService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final InvoiceDao invoiceDao;

    public InvoicingService(InvoiceDao invoiceDao) {
        this.invoiceDao = invoiceDao;
    }

    public Optional<Long> getLastEventId() throws DaoException {
        Optional<Long> lastEventId = Optional.ofNullable(invoiceDao.getLastEventId());
        log.info("Last invoicing eventId={}", lastEventId);
        return lastEventId;
    }
}
