package com.rbkmoney.newway.listener;

import com.rbkmoney.eventstock.client.DefaultSubscriberConfig;
import com.rbkmoney.eventstock.client.EventConstraint;
import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.SubscriberConfig;
import com.rbkmoney.eventstock.client.poll.EventFlowFilter;
import com.rbkmoney.newway.service.InvoicingService;
import com.rbkmoney.newway.service.PayoutService;
import com.rbkmoney.newway.service.PartyManagementService;
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

    private final PartyManagementService partyManagementService;
    private final InvoicingService invoicingService;
    private final PayoutService payoutService;

    @Value("${bm.pollingEnabled}")
    private boolean pollingEnabled;

    public OnStart(EventPublisher partyManagementEventPublisher,
                   EventPublisher invoicingEventPublisher,
                   EventPublisher payoutEventPublisher,
                   PartyManagementService partyManagementService,
                   InvoicingService invoicingService,
                   PayoutService payoutService) {
        this.partyManagementEventPublisher = partyManagementEventPublisher;
        this.invoicingEventPublisher = invoicingEventPublisher;
        this.payoutEventPublisher = payoutEventPublisher;

        this.partyManagementService = partyManagementService;
        this.invoicingService = invoicingService;
        this.payoutService = payoutService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (pollingEnabled) {
            partyManagementEventPublisher.subscribe(buildSubscriberConfig(partyManagementService.getLastEventId()));
            invoicingEventPublisher.subscribe(buildSubscriberConfig(invoicingService.getLastEventId()));
            payoutEventPublisher.subscribe(buildSubscriberConfig(payoutService.getLastEventId()));
        }
    }

    private SubscriberConfig buildSubscriberConfig(Optional<Long> lastEventIdOptional) {
        EventConstraint.EventIDRange eventIDRange = new EventConstraint.EventIDRange();
        lastEventIdOptional.ifPresent(eventIDRange::setFromExclusive);
        EventFlowFilter eventFlowFilter = new EventFlowFilter(new EventConstraint(eventIDRange));
        return new DefaultSubscriberConfig(eventFlowFilter);
    }
}
