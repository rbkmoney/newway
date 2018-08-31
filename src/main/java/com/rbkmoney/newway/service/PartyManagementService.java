package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.exception.DaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PartyManagementService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PartyDao partyDao;

    public PartyManagementService(PartyDao partyDao) {
        this.partyDao = partyDao;
    }

    public Optional<Long> getLastEventId() throws DaoException {
        Optional<Long> lastEventId = Optional.ofNullable(partyDao.getLastEventId());
        log.info("Last party management eventId={}", lastEventId);
        return lastEventId;
    }
}
