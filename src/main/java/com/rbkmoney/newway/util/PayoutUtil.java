package com.rbkmoney.newway.util;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.newway.domain.tables.pojos.PayoutSummary;

import java.util.List;
import java.util.stream.Collectors;

public class PayoutUtil {
    public static List<PayoutSummary> convertPayoutSummaries(com.rbkmoney.damsel.payout_processing.Payout payoutCreated,
                                                             long pytId) {
        return payoutCreated.getSummary().stream().map(s -> {
            PayoutSummary ps = new PayoutSummary();
            ps.setPytId(pytId);
            ps.setAmount(s.getAmount());
            ps.setFee(s.getFee());
            ps.setCurrencyCode(s.getCurrencySymbolicCode());
            ps.setFromTime(com.rbkmoney.geck.common.util.TypeUtil.stringToLocalDateTime(s.getFromTime()));
            ps.setToTime(TypeUtil.stringToLocalDateTime(s.getToTime()));
            ps.setOperationType(s.getOperationType().name());
            ps.setCount(s.getCount());
            return ps;
        }).collect(Collectors.toList());
    }
}
