package com.rbkmoney.newway.poller.event_stock.impl.payout;

import com.rbkmoney.damsel.payout_processing.*;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.payout.iface.PayoutDao;
import com.rbkmoney.newway.dao.payout.iface.PayoutSummaryDao;
import com.rbkmoney.newway.domain.tables.pojos.Payout;
import com.rbkmoney.newway.domain.tables.pojos.PayoutSummary;
import com.rbkmoney.newway.exception.NotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayoutStatusChangedHandler extends AbstractPayoutHandler {

    private final PayoutDao payoutDao;
    private final PayoutSummaryDao payoutSummaryDao;

    @Getter
    private final Filter filter = new PathConditionFilter(new PathConditionRule(
            "payout_status_changed",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PayoutChange change, Event event, Integer changeId) {
        long eventId = event.getId();
        PayoutStatusChanged payoutStatusChanged = change.getPayoutStatusChanged();
        PayoutStatus payoutStatus = payoutStatusChanged.getStatus();
        String payoutId = event.getSource().getPayoutId();
        log.info("Start payout status changed handling, eventId={}, payoutId={}", eventId, payoutId);
        Payout payoutSource = payoutDao.get(payoutId);
        if (payoutSource == null) {
            throw new NotFoundException(String.format("Payout not found, payoutId='%s'", payoutId));
        }
        Long oldPayoutId = payoutSource.getId();
        payoutSource.setId(null);
        payoutSource.setWtime(null);
        payoutSource.setEventId(eventId);
        payoutSource.setChangeId(changeId);
        payoutSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        payoutSource.setStatus(TBaseUtil.unionFieldToEnum(payoutStatus, com.rbkmoney.newway.domain.enums.PayoutStatus.class));
        if (payoutStatus.isSetPaid()) {
            payoutSource.setStatusCancelledUserInfoId(null);
            payoutSource.setStatusCancelledUserInfoType(null);
            payoutSource.setStatusCancelledDetails(null);
            payoutSource.setStatusConfirmedUserInfoType(null);
        } else if (payoutStatus.isSetCancelled()) {
            payoutSource.setStatusPaidDetails(null);
            payoutSource.setStatusPaidDetailsCardProviderName(null);
            payoutSource.setStatusPaidDetailsCardProviderTransactionId(null);
            PayoutCancelled cancelled = payoutStatus.getCancelled();
            payoutSource.setStatusCancelledDetails(cancelled.getDetails());
            payoutSource.setStatusConfirmedUserInfoType(null);
        } else if (payoutStatus.isSetConfirmed()) {
            payoutSource.setStatusPaidDetails(null);
            payoutSource.setStatusPaidDetailsCardProviderName(null);
            payoutSource.setStatusPaidDetailsCardProviderTransactionId(null);
            payoutSource.setStatusCancelledUserInfoId(null);
            payoutSource.setStatusCancelledUserInfoType(null);
            payoutSource.setStatusCancelledDetails(null);
        }

        payoutDao.save(payoutSource).ifPresentOrElse(
                id -> {
                    payoutDao.updateNotCurrent(oldPayoutId);
                    List<PayoutSummary> payoutSummaries = payoutSummaryDao.getByPytId(oldPayoutId);
                    if (!CollectionUtils.isEmpty(payoutSummaries)) {
                        payoutSummaries.forEach(pt -> {
                            pt.setId(null);
                            pt.setPytId(id);
                        });
                        payoutSummaryDao.save(payoutSummaries);
                    }
                    log.info("Payout status  has been saved, eventId={}, changeId={}, payoutId={}", eventId, changeId, payoutId);
                },
                () -> log.info("Payout status  bound duplicated, eventId={}, changeId={}, payoutId={}",
                        eventId, changeId, payoutId)
        );
    }

}
