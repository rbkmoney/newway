package com.rbkmoney.newway.listener;

import com.rbkmoney.eventstock.client.DefaultSubscriberConfig;
import com.rbkmoney.eventstock.client.EventConstraint;
import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.SubscriberConfig;
import com.rbkmoney.eventstock.client.poll.EventFlowFilter;
import com.rbkmoney.newway.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
public class OnStart implements ApplicationListener<ApplicationReadyEvent> {
    private final EventPublisher partyManagementEventPublisher;
    private final EventPublisher payoutEventPublisher;
    private final EventPublisher identityEventPublisher;
    private final EventPublisher withdrawalEventPublisher;
    private final EventPublisher walletEventPublisher;
    private final EventPublisher depositEventPublisher;
    private final EventPublisher sourceEventPublisher;
    private final EventPublisher destinationEventPublisher;
    private final EventPublisher withdrawalSessionEventPublisher;
    private final EventPublisher rateEventPublisher;

    private final PartyManagementService partyManagementService;
    private final PayoutService payoutService;
    private final WalletService walletService;
    private final IdentityService identityService;
    private final WithdrawalService withdrawalService;
    private final SourceService sourceService;
    private final DestinationService destinationService;
    private final DepositService depositService;
    private final WithdrawalSessionService withdrawalSessionService;
    private final RateService rateService;

    @Value("${bm.polling.enabled}")
    private boolean pollingEnabled;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (pollingEnabled) {
            partyManagementEventPublisher.subscribe(buildSubscriberConfig(partyManagementService.getLastEventId()));
            payoutEventPublisher.subscribe(buildSubscriberConfig(payoutService.getLastEventId()));
            identityEventPublisher.subscribe(buildSubscriberConfig(identityService.getLastEventId()));
            walletEventPublisher.subscribe(buildSubscriberConfig(walletService.getLastEventId()));
            sourceEventPublisher.subscribe(buildSubscriberConfig(sourceService.getLastEventId()));
            destinationEventPublisher.subscribe(buildSubscriberConfig(destinationService.getLastEventId()));
            depositEventPublisher.subscribe(buildSubscriberConfig(depositService.getLastEventId()));
            withdrawalEventPublisher.subscribe(buildSubscriberConfig(withdrawalService.getLastEventId()));
            withdrawalSessionEventPublisher.subscribe(buildSubscriberConfig(withdrawalSessionService.getLastEventId()));
            rateEventPublisher.subscribe(buildSubscriberConfig(rateService.getLastEventId()));
        }
    }

    private SubscriberConfig buildSubscriberConfig(Optional<Long> lastEventIdOptional) {
        EventConstraint.EventIDRange eventIDRange = new EventConstraint.EventIDRange();
        lastEventIdOptional.ifPresent(eventIDRange::setFromExclusive);
        EventFlowFilter eventFlowFilter = new EventFlowFilter(new EventConstraint(eventIDRange));
        return new DefaultSubscriberConfig(eventFlowFilter);
    }
}
