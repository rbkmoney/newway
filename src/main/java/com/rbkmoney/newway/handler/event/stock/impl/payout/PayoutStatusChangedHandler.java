package com.rbkmoney.newway.handler.event.stock.impl.payout;

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
public class PayoutStatusChangedHandler implements PayoutHandler {

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
        String payoutId = event.getSource().getPayoutId();
        log.info("Start payout status changed handling, eventId={}, payoutId={}", eventId, payoutId);
        Payout payoutSourceOld = payoutDao.get(payoutId);
        Payout payoutNew = new Payout(payoutSourceOld);
        payoutNew.setId(null);
        payoutNew.setWtime(null);
        payoutNew.setEventId(eventId);
        payoutNew.setChangeId(changeId);
        payoutNew.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        PayoutStatusChanged payoutStatusChanged = change.getPayoutStatusChanged();
        PayoutStatus payoutStatus = payoutStatusChanged.getStatus();
        payoutNew.setStatus(
                TBaseUtil.unionFieldToEnum(payoutStatus, com.rbkmoney.newway.domain.enums.PayoutStatus.class));

        if (payoutStatus.isSetPaid()) {
            payoutNew.setStatusCancelledUserInfoId(null);
            payoutNew.setStatusCancelledUserInfoType(null);
            payoutNew.setStatusCancelledDetails(null);
            payoutNew.setStatusConfirmedUserInfoType(null);
        } else if (payoutStatus.isSetCancelled()) {
            payoutNew.setStatusPaidDetails(null);
            payoutNew.setStatusPaidDetailsCardProviderName(null);
            payoutNew.setStatusPaidDetailsCardProviderTransactionId(null);
            PayoutCancelled cancelled = payoutStatus.getCancelled();
            payoutNew.setStatusCancelledDetails(cancelled.getDetails());
            payoutNew.setStatusConfirmedUserInfoType(null);
        } else if (payoutStatus.isSetConfirmed()) {
            payoutNew.setStatusPaidDetails(null);
            payoutNew.setStatusPaidDetailsCardProviderName(null);
            payoutNew.setStatusPaidDetailsCardProviderTransactionId(null);
            payoutNew.setStatusCancelledUserInfoId(null);
            payoutNew.setStatusCancelledUserInfoType(null);
            payoutNew.setStatusCancelledDetails(null);
        }

        payoutDao.save(payoutNew).ifPresentOrElse(
                id -> {
                    Long oldId = payoutSourceOld.getId();
                    payoutDao.updateNotCurrent(oldId);
                    List<PayoutSummary> payoutSummaries = payoutSummaryDao.getByPytId(oldId);
                    if (!CollectionUtils.isEmpty(payoutSummaries)) {
                        payoutSummaries.forEach(pt -> {
                            pt.setId(null);
                            pt.setPytId(id);
                        });
                        payoutSummaryDao.save(payoutSummaries);
                    }
                    log.info("Payout status  has been saved, eventId={}, changeId={}, payoutId={}", eventId, changeId,
                            payoutId);
                },
                () -> log.info("Payout status  bound duplicated, eventId={}, changeId={}, payoutId={}",
                        eventId, changeId, payoutId)
        );
    }

}
