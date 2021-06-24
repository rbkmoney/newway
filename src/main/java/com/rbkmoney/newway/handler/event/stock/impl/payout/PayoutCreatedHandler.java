package com.rbkmoney.newway.handler.event.stock.impl.payout;

import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.payout.iface.PayoutDao;
import com.rbkmoney.newway.domain.enums.PayoutStatus;
import com.rbkmoney.newway.domain.tables.pojos.Payout;
import com.rbkmoney.payout.manager.Event;
import com.rbkmoney.payout.manager.PayoutChange;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayoutCreatedHandler implements PayoutHandler {

    private final PayoutDao payoutDao;

    @Getter
    private final Filter filter = new PathConditionFilter(new PathConditionRule(
            "payout_created",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PayoutChange change, Event event) {
        var payoutSource = change.getCreated().getPayout();
        String payoutId = payoutSource.getPayoutId();
        String partyId = payoutSource.getPartyId();
        log.info("Start payout created handling, sequenceId={}, partyId={}, payoutId={}",
                event.getSequenceId(), partyId, payoutId);
        Payout payout = new Payout();
        payout.setSequenceId(event.getSequenceId());
        payout.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        payout.setPayoutId(payoutId);
        payout.setPartyId(partyId);
        payout.setShopId(payoutSource.getShopId());
        payout.setCreatedAt(TypeUtil.stringToLocalDateTime(payoutSource.getCreatedAt()));
        payout.setStatus(TBaseUtil.unionFieldToEnum(payoutSource.getStatus(), PayoutStatus.class));
        payout.setAmount(payoutSource.getAmount());
        payout.setFee(payoutSource.getFee());
        payout.setPayoutToolId(payoutSource.getPayoutToolId());
        payout.setCurrencyCode(payoutSource.getCurrency().getSymbolicCode());

        Optional<Long> id = payoutDao.save(payout);
        if (id.isEmpty()) {
            log.info("Payout has been bound duplicated, sequenceId={}, changeId={}, payoutId={}",
                    event.getSequenceId(), partyId, payoutId);
        }
    }
}
