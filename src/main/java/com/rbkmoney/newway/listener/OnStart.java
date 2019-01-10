package com.rbkmoney.newway.listener;

import com.rbkmoney.eventstock.client.DefaultSubscriberConfig;
import com.rbkmoney.eventstock.client.EventConstraint;
import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.SubscriberConfig;
import com.rbkmoney.eventstock.client.poll.EventFlowFilter;
import com.rbkmoney.newway.poller.event_stock.InvoicingEventStockHandler;
import com.rbkmoney.newway.poller.event_stock.InvoicingMod0EventStockHandler;
import com.rbkmoney.newway.poller.event_stock.InvoicingMod1EventStockHandler;
import com.rbkmoney.newway.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OnStart implements ApplicationListener<ApplicationReadyEvent> {
    private final EventPublisher partyManagementEventPublisher;
    private final EventPublisher invoicingMod0EventPublisher;
    private final EventPublisher invoicingMod1EventPublisher;
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

    private final InvoicingMod0EventStockHandler invoicingMod0EventStockHandler;
    private final InvoicingMod1EventStockHandler invoicingMod1EventStockHandler;

    @Value("${bm.pollingEnabled}")
    private boolean pollingEnabled;

    public OnStart(EventPublisher partyManagementEventPublisher,
                   EventPublisher invoicingMod0EventPublisher,
                   EventPublisher invoicingMod1EventPublisher,
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
                   InvoicingMod0EventStockHandler invoicingMod0EventStockHandler,
                   InvoicingMod1EventStockHandler invoicingMod1EventStockHandler) {
        this.partyManagementEventPublisher = partyManagementEventPublisher;
        this.invoicingMod0EventPublisher = invoicingMod0EventPublisher;
        this.invoicingMod1EventPublisher = invoicingMod1EventPublisher;
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
        this.invoicingMod0EventStockHandler = invoicingMod0EventStockHandler;
        this.invoicingMod1EventStockHandler = invoicingMod1EventStockHandler;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (pollingEnabled) {
            partyManagementEventPublisher.subscribe(buildSubscriberConfig(partyManagementService.getLastEventId()));
            invoicingMod0EventPublisher.subscribe(buildSubscriberConfig(invoicingService.getLastEventId(InvoicingEventStockHandler.DIVIDER, invoicingMod0EventStockHandler.getMod())));
            invoicingMod1EventPublisher.subscribe(buildSubscriberConfig(invoicingService.getLastEventId(InvoicingEventStockHandler.DIVIDER, invoicingMod1EventStockHandler.getMod())));
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
