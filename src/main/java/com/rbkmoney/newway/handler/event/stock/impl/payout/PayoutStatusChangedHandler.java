package com.rbkmoney.newway.handler.event.stock.impl.payout;

import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.payout.iface.PayoutDao;
import com.rbkmoney.newway.domain.tables.pojos.Payout;
import com.rbkmoney.payout.manager.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayoutStatusChangedHandler implements PayoutHandler {

    private final PayoutDao payoutDao;

    @Getter
    private final Filter filter = new PathConditionFilter(new PathConditionRule(
            "payout_status_changed",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PayoutChange change, Event event) {
        String payoutId = event.getPayoutId();
        log.info("Start payout status changed handling, sequenceId={}, payoutId={}", event.getSequenceId(), payoutId);
        Payout payoutSourceOld = payoutDao.get(payoutId);
        Payout payoutNew = new Payout(payoutSourceOld);
        payoutNew.setId(null);
        payoutNew.setWtime(null);
        payoutNew.setSequenceId(event.getSequenceId());
        payoutNew.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        PayoutStatusChanged payoutStatusChanged = change.getStatusChanged();
        PayoutStatus payoutStatus = payoutStatusChanged.getStatus();
        payoutNew.setStatus(
                TBaseUtil.unionFieldToEnum(payoutStatus, com.rbkmoney.newway.domain.enums.PayoutStatus.class));

        if (payoutStatus.isSetCancelled()) {
            payoutNew.setCancelledDetails(payoutStatus.getCancelled().getDetails());
        }

        payoutDao.save(payoutNew).ifPresentOrElse(
                id -> {
                    Long oldId = payoutSourceOld.getId();
                    payoutDao.updateNotCurrent(oldId);
                    log.info("Payout status  has been saved, sequenceId={}, payoutId={}",
                            event.getSequenceId(), payoutId);
                },
                () -> log.info("Payout status  bound duplicated, sequenceId={}, payoutId={}",
                        event.getSequenceId(), payoutId)
        );
    }

}
