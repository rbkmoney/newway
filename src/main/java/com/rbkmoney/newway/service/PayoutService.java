package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.payout.iface.PayoutDao;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PayoutService {

    private final PayoutDao payoutDao;

    public PayoutService(PayoutDao payoutDao) {
        this.payoutDao = payoutDao;
    }

    public Optional<Long> getLastEventId() {
        return Optional.ofNullable(payoutDao.getLastEventId());
    }
}
