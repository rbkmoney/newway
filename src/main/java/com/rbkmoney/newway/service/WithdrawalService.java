package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.withdrawal.iface.WithdrawalDao;
import com.rbkmoney.newway.exception.DaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WithdrawalService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WithdrawalDao withdrawalDao;

    public WithdrawalService(WithdrawalDao withdrawalDao) {
        this.withdrawalDao = withdrawalDao;
    }

    public Optional<Long> getLastEventId() throws DaoException {
        Optional<Long> lastEventId = Optional.ofNullable(withdrawalDao.getLastEventId());
        log.info("Last withdrawal eventId={}", lastEventId);
        return lastEventId;
    }

}
