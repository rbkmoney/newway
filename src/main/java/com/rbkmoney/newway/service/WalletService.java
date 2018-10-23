package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.wallet.iface.WalletDao;
import com.rbkmoney.newway.exception.DaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WalletService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WalletDao walletDao;

    public WalletService(WalletDao walletDao) {
        this.walletDao = walletDao;
    }

    public Optional<Long> getLastEventId() throws DaoException {
        Optional<Long> lastEventId = Optional.ofNullable(walletDao.getLastEventId());
        log.info("Last wallet eventId={}", lastEventId);
        return lastEventId;
    }

}
