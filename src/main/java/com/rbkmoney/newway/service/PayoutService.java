package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.payout.iface.PayoutDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PayoutService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PayoutDao payoutDao;

    public PayoutService(PayoutDao payoutDao) {
        this.payoutDao = payoutDao;
    }

    public Optional<Long> getLastEventId() {
        Optional<Long> lastEventId = Optional.ofNullable(payoutDao.getLastEventId());
        log.info("Last payout eventId={}", lastEventId);
        return lastEventId;
    }
}
