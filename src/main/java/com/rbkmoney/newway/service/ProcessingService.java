package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.invoicing.iface.InvoiceDao;
import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.exception.DaoException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProcessingService {

    private final PartyDao partyDao;
    private final InvoiceDao invoiceDao;

    public ProcessingService(PartyDao partyDao, InvoiceDao invoiceDao) {
        this.partyDao = partyDao;
        this.invoiceDao = invoiceDao;
    }

    public Optional<Long> getLastEventId() throws DaoException {
        Long partyLastEventId = partyDao.getLastEventId();
        Long invLastEventId = invoiceDao.getLastEventId();
        Long max = partyLastEventId;
        if (partyLastEventId == null) {
            max = invLastEventId;
        } else if (invLastEventId != null) {
            max = Math.max(partyLastEventId, invLastEventId);
        }
        return Optional.ofNullable(max);
    }
}
