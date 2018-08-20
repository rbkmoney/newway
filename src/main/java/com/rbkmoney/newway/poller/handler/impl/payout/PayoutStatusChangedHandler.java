package com.rbkmoney.newway.poller.handler.impl.payout;

import com.rbkmoney.damsel.payout_processing.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.payout.iface.PayoutDao;
import com.rbkmoney.newway.dao.payout.iface.PayoutSummaryDao;
import com.rbkmoney.newway.domain.enums.Payoutpaidstatusdetails;
import com.rbkmoney.newway.domain.enums.Payoutstatus;
import com.rbkmoney.newway.domain.enums.Usertype;
import com.rbkmoney.newway.domain.tables.pojos.Payout;
import com.rbkmoney.newway.domain.tables.pojos.PayoutSummary;
import com.rbkmoney.newway.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class PayoutStatusChangedHandler extends AbstractPayoutHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PayoutDao payoutDao;
    private final PayoutSummaryDao payoutSummaryDao;

    private final Filter filter;

    public PayoutStatusChangedHandler(PayoutDao payoutDao, PayoutSummaryDao payoutSummaryDao) {
        this.payoutDao = payoutDao;
        this.payoutSummaryDao = payoutSummaryDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "payout_status_changed",
                new IsNullCondition().not()));
    }

    @Override
    public void handle(PayoutChange change, Event event) {
        long eventId = event.getId();
        PayoutStatusChanged payoutStatusChanged = change.getPayoutStatusChanged();
        PayoutStatus payoutStatus = payoutStatusChanged.getStatus();
        String payoutId = event.getSource().getPayoutId();
        log.info("Start payout status changed handling, eventId={}, payoutId={}", eventId, payoutId);
        Payout payoutSource = payoutDao.get(payoutId);
        if (payoutSource == null) {
            throw new NotFoundException(String.format("Payout not found, payoutId='%s'", payoutId));
        }
        Long payoutSourceId = payoutSource.getId();
        payoutSource.setId(null);
        payoutSource.setEventId(eventId);
        payoutSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        Payoutstatus payoutstatus = TypeUtil.toEnumField(payoutStatus.getSetField().getFieldName(), Payoutstatus.class);
        if (payoutstatus == null) {
            throw new IllegalArgumentException("Illegal payout status: " + payoutStatus);
        }
        payoutSource.setStatus(payoutstatus);
        if (payoutStatus.isSetPaid()) {
            PaidDetails details = payoutStatus.getPaid().getDetails();
            Payoutpaidstatusdetails paidDetails = TypeUtil.toEnumField(details.getSetField().getFieldName(), Payoutpaidstatusdetails.class);
            if (paidDetails == null) {
                throw new IllegalArgumentException("Illegal paid details: " + details);
            }
            payoutSource.setStatusPaidDetails(paidDetails);
            if (details.isSetCardDetails()) {
                payoutSource.setStatusPaidDetailsCardProviderName(details.getCardDetails().getProviderDetails().getName());
                payoutSource.setStatusPaidDetailsCardProviderTransactionId(details.getCardDetails().getProviderDetails().getTransactionId());
            }
            payoutSource.setStatusCancelledUserInfoId(null);
            payoutSource.setStatusCancelledUserInfoType(null);
            payoutSource.setStatusCancelledDetails(null);
            payoutSource.setStatusConfirmedUserInfoType(null);
        } else if (payoutStatus.isSetCancelled()) {
            payoutSource.setStatusPaidDetails(null);
            payoutSource.setStatusPaidDetailsCardProviderName(null);
            payoutSource.setStatusPaidDetailsCardProviderTransactionId(null);
            PayoutCancelled cancelled = payoutStatus.getCancelled();
            payoutSource.setStatusCancelledUserInfoId(cancelled.getUserInfo().getId());
            Usertype statusCancelledUserInfoType = TypeUtil.toEnumField(cancelled.getUserInfo().getType().getSetField().getFieldName(), Usertype.class);
            if (statusCancelledUserInfoType == null) {
                throw new IllegalArgumentException("Illegal user type: " + cancelled.getUserInfo().getType());
            }
            payoutSource.setStatusCancelledUserInfoType(statusCancelledUserInfoType);
            payoutSource.setStatusCancelledDetails(cancelled.getDetails());
            payoutSource.setStatusConfirmedUserInfoType(null);
        } if (payoutStatus.isSetConfirmed()) {
            payoutSource.setStatusPaidDetails(null);
            payoutSource.setStatusPaidDetailsCardProviderName(null);
            payoutSource.setStatusPaidDetailsCardProviderTransactionId(null);
            payoutSource.setStatusCancelledUserInfoId(null);
            payoutSource.setStatusCancelledUserInfoType(null);
            payoutSource.setStatusCancelledDetails(null);
            UserInfo confirmedUserInfo = payoutStatus.getConfirmed().getUserInfo();
            Usertype statusCondirmedUserInfoType = TypeUtil.toEnumField(confirmedUserInfo.getType().getSetField().getFieldName(), Usertype.class);
            if (statusCondirmedUserInfoType == null) {
                throw new IllegalArgumentException("Illegal user type: " + confirmedUserInfo.getType());
            }
            payoutSource.setStatusConfirmedUserInfoType(statusCondirmedUserInfoType);
        }
        long pytId = payoutDao.save(payoutSource);

        List<PayoutSummary> payoutSummaries = payoutSummaryDao.getByPytId(payoutSourceId);
        payoutSummaries.forEach(pt -> {
            pt.setId(null);
            pt.setPytId(pytId);
        });
        payoutSummaryDao.save(payoutSummaries);

        log.info("Payout status has been saved, eventId={}, payoutId={}", eventId, payoutId);
    }

    @Override
    public Filter<PayoutChange> getFilter() {
        return filter;
    }
}
