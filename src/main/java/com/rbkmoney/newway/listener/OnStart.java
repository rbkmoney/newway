package com.rbkmoney.newway.listener;

import com.rbkmoney.eventstock.client.DefaultSubscriberConfig;
import com.rbkmoney.eventstock.client.EventConstraint;
import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.SubscriberConfig;
import com.rbkmoney.eventstock.client.poll.EventFlowFilter;
import com.rbkmoney.newway.poller.event_stock.InvoicingEventStockHandler;
import com.rbkmoney.newway.poller.event_stock.InvoicingEventStockHandlerMod0;
import com.rbkmoney.newway.poller.event_stock.InvoicingEventStockHandlerMod1;
import com.rbkmoney.newway.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OnStart implements ApplicationListener<ApplicationReadyEvent> {
    private final EventPublisher partyManagementEventPublisher;
    private final EventPublisher invoicingEventPublisherMod0;
    private final EventPublisher invoicingEventPublisherMod1;
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
    private final InvoicingService invoicingService;
    private final PayoutService payoutService;
    private final WalletService walletService;
    private final IdentityService identityService;
    private final WithdrawalService withdrawalService;
    private final SourceService sourceService;
    private final DestinationService destinationService;
    private final DepositService depositService;
    private final WithdrawalSessionService withdrawalSessionService;
    private final RateService rateService;

    private final InvoicingEventStockHandlerMod0 invoicingEventStockHandlerMod0;
    private final InvoicingEventStockHandlerMod1 invoicingEventStockHandlerMod1;

    @Value("${bm.pollingEnabled}")
    private boolean pollingEnabled;

    public OnStart(EventPublisher partyManagementEventPublisher,
                   EventPublisher invoicingEventPublisherMod0,
                   EventPublisher invoicingEventPublisherMod1,
                   EventPublisher payoutEventPublisher,
                   EventPublisher identityEventPublisher,
                   EventPublisher withdrawalEventPublisher,
                   EventPublisher walletEventPublisher,
                   EventPublisher sourceEventPublisher,
                   EventPublisher destinationEventPublisher,
                   EventPublisher depositEventPublisher,
                   EventPublisher withdrawalSessionEventPublisher,
                   EventPublisher rateEventPublisher,

                   PartyManagementService partyManagementService,
                   InvoicingService invoicingService,
                   PayoutService payoutService,
                   WalletService walletService,
                   IdentityService identityService,
                   WithdrawalService withdrawalService,
                   SourceService sourceService,
                   DestinationService destinationService,
                   DepositService depositService,
                   WithdrawalSessionService withdrawalSessionService,
                   RateService rateService,
                   InvoicingEventStockHandlerMod0 invoicingEventStockHandlerMod0,
                   InvoicingEventStockHandlerMod1 invoicingEventStockHandlerMod1) {
        this.partyManagementEventPublisher = partyManagementEventPublisher;
        this.invoicingEventPublisherMod0 = invoicingEventPublisherMod0;
        this.invoicingEventPublisherMod1 = invoicingEventPublisherMod1;
        this.payoutEventPublisher = payoutEventPublisher;
        this.identityEventPublisher = identityEventPublisher;
        this.walletEventPublisher = walletEventPublisher;
        this.withdrawalEventPublisher = withdrawalEventPublisher;
        this.depositEventPublisher = depositEventPublisher;
        this.sourceEventPublisher = sourceEventPublisher;
        this.destinationEventPublisher = destinationEventPublisher;
        this.withdrawalSessionEventPublisher = withdrawalSessionEventPublisher;
        this.rateEventPublisher = rateEventPublisher;

        this.partyManagementService = partyManagementService;
        this.invoicingService = invoicingService;
        this.payoutService = payoutService;
        this.walletService = walletService;
        this.identityService = identityService;
        this.withdrawalService = withdrawalService;
        this.sourceService = sourceService;
        this.destinationService = destinationService;
        this.depositService = depositService;
        this.withdrawalSessionService = withdrawalSessionService;
        this.rateService = rateService;
        this.invoicingEventStockHandlerMod0 = invoicingEventStockHandlerMod0;
        this.invoicingEventStockHandlerMod1 = invoicingEventStockHandlerMod1;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (pollingEnabled) {
            partyManagementEventPublisher.subscribe(buildSubscriberConfig(partyManagementService.getLastEventId()));
            invoicingEventPublisherMod0.subscribe(buildSubscriberConfig(invoicingService.getLastEventId(InvoicingEventStockHandler.DIVIDER, invoicingEventStockHandlerMod0.getMod())));
            invoicingEventPublisherMod1.subscribe(buildSubscriberConfig(invoicingService.getLastEventId(InvoicingEventStockHandler.DIVIDER, invoicingEventStockHandlerMod1.getMod())));
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
