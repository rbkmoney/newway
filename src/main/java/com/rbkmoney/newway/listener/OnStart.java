package com.rbkmoney.newway.listener;

import com.rbkmoney.eventstock.client.DefaultSubscriberConfig;
import com.rbkmoney.eventstock.client.EventConstraint;
import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.SubscriberConfig;
import com.rbkmoney.eventstock.client.poll.EventFlowFilter;
import com.rbkmoney.newway.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OnStart implements ApplicationListener<ApplicationReadyEvent> {
    private final EventPublisher partyManagementEventPublisher;
    private final EventPublisher invoicingEventPublisher;
    private final EventPublisher payoutEventPublisher;
    private final EventPublisher identityEventPublisher;
    private final EventPublisher withdrawalEventPublisher;
    private final EventPublisher walletEventPublisher;

    private final PartyManagementService partyManagementService;
    private final InvoicingService invoicingService;
    private final PayoutService payoutService;
    private final WalletService walletService;
    private final IdentityService identityService;
    private final WithdrawalService withdrawalService;

    @Value("${bm.pollingEnabled}")
    private boolean pollingEnabled;

    public OnStart(EventPublisher partyManagementEventPublisher,
                   EventPublisher invoicingEventPublisher,
                   EventPublisher payoutEventPublisher,
                   EventPublisher identityEventPublisher,
                   EventPublisher withdrawalEventPublisher,
                   EventPublisher walletEventPublisher,
                   PartyManagementService partyManagementService,
                   InvoicingService invoicingService,
                   PayoutService payoutService,
                   WalletService walletService,
                   IdentityService identityService,
                   WithdrawalService withdrawalService) {
        this.partyManagementEventPublisher = partyManagementEventPublisher;
        this.invoicingEventPublisher = invoicingEventPublisher;
        this.payoutEventPublisher = payoutEventPublisher;
        this.identityEventPublisher = identityEventPublisher;
        this.walletEventPublisher = walletEventPublisher;
        this.withdrawalEventPublisher = withdrawalEventPublisher;

        this.partyManagementService = partyManagementService;
        this.invoicingService = invoicingService;
        this.payoutService = payoutService;
        this.walletService = walletService;
        this.identityService = identityService;
        this.withdrawalService = withdrawalService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (pollingEnabled) {
            partyManagementEventPublisher.subscribe(buildSubscriberConfig(partyManagementService.getLastEventId()));
            invoicingEventPublisher.subscribe(buildSubscriberConfig(invoicingService.getLastEventId()));
            payoutEventPublisher.subscribe(buildSubscriberConfig(payoutService.getLastEventId()));
            identityEventPublisher.subscribe(buildSubscriberConfig(identityService.getLastEventId()));
            walletEventPublisher.subscribe(buildSubscriberConfig(walletService.getLastEventId()));
            withdrawalEventPublisher.subscribe(buildSubscriberConfig(withdrawalService.getLastEventId()));
        }
    }

    private SubscriberConfig buildSubscriberConfig(Optional<Long> lastEventIdOptional) {
        EventConstraint.EventIDRange eventIDRange = new EventConstraint.EventIDRange();
        lastEventIdOptional.ifPresent(eventIDRange::setFromExclusive);
        EventFlowFilter eventFlowFilter = new EventFlowFilter(new EventConstraint(eventIDRange));
        return new DefaultSubscriberConfig(eventFlowFilter);
    }
}
