package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.exception.DaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IdentityService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final IdentityDao identityDao;

    public IdentityService(IdentityDao identityDao) {
        this.identityDao = identityDao;
    }

    public Optional<Long> getLastEventId() throws DaoException {
        Optional<Long> lastEventId = Optional.ofNullable(identityDao.getLastEventId());
        log.info("Last identity eventId={}", lastEventId);
        return lastEventId;
    }

}
