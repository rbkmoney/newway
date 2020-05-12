package com.rbkmoney.newway.config;

import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.poll.FistfulPollingEventPublisherBuilder;
import com.rbkmoney.eventstock.client.poll.PollingEventPublisherBuilder;
import com.rbkmoney.eventstock.client.poll.RatesPollingEventPublisherBuilder;
import com.rbkmoney.newway.listener.OnStart;
import com.rbkmoney.newway.poller.event_stock.*;
import com.rbkmoney.newway.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class EventStockConfig {

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "true")
    public OnStart eventSinkListener(
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
            PayoutService payoutService,
            WalletService walletService,
            IdentityService identityService,
            WithdrawalService withdrawalService,
            SourceService sourceService,
            DestinationService destinationService,
            DepositService depositService,
            WithdrawalSessionService withdrawalSessionService,
            RateService rateService) {
        return new OnStart(
                payoutEventPublisher,
                identityEventPublisher,
                withdrawalEventPublisher,
                walletEventPublisher,
                sourceEventPublisher,
                destinationEventPublisher,
                depositEventPublisher,
                withdrawalSessionEventPublisher,
                rateEventPublisher,
                partyManagementService,
                payoutService,
                walletService,
                identityService,
                withdrawalService,
                sourceService,
                destinationService,
                depositService,
                withdrawalSessionService,
                rateService);
    }

    @Bean
    public EventPublisher payoutEventPublisher(
            PayoutEventStockHandler payoutEventStockHandler,
            @Value("${payouter.polling.url}") Resource resource,
            @Value("${payouter.polling.delay}") int pollDelay,
            @Value("${payouter.polling.retryDelay}") int retryDelay,
            @Value("${payouter.polling.maxPoolSize}") int maxPoolSize
    ) throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(resource.getURI())
                .withEventHandler(payoutEventStockHandler)
                .withMaxPoolSize(maxPoolSize)
                .withEventRetryDelay(retryDelay)
                .withPollDelay(pollDelay)
                .build();
    }

    @Bean
    public EventPublisher walletEventPublisher(
            WalletEventStockHandler walletEventStockHandler,
            @Value("${wallet.polling.url}") Resource resource,
            @Value("${wallet.polling.delay}") int pollDelay,
            @Value("${wallet.polling.retryDelay}") int retryDelay,
            @Value("${wallet.polling.maxPoolSize}") int maxPoolSize
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withWalletServiceAdapter()
                .withURI(resource.getURI())
                .withEventHandler(walletEventStockHandler)
                .withMaxPoolSize(maxPoolSize)
                .withEventRetryDelay(retryDelay)
                .withPollDelay(pollDelay)
                .build();
    }

    @Bean
    public EventPublisher identityEventPublisher(
            IdentityEventStockHandler identityEventStockHandler,
            @Value("${identity.polling.url}") Resource resource,
            @Value("${identity.polling.delay}") int pollDelay,
            @Value("${identity.polling.retryDelay}") int retryDelay,
            @Value("${identity.polling.maxPoolSize}") int maxPoolSize
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withIdentityServiceAdapter()
                .withURI(resource.getURI())
                .withEventHandler(identityEventStockHandler)
                .withMaxPoolSize(maxPoolSize)
                .withEventRetryDelay(retryDelay)
                .withPollDelay(pollDelay)
                .build();
    }

    @Bean
    public EventPublisher withdrawalEventPublisher(
            WithdrawalEventStockHandler withdrawalEventStockHandler,
            @Value("${withdrawal.polling.url}") Resource resource,
            @Value("${withdrawal.polling.delay}") int pollDelay,
            @Value("${withdrawal.polling.retryDelay}") int retryDelay,
            @Value("${withdrawal.polling.maxPoolSize}") int maxPoolSize
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withWithdrawalServiceAdapter()
                .withURI(resource.getURI())
                .withEventHandler(withdrawalEventStockHandler)
                .withMaxPoolSize(maxPoolSize)
                .withEventRetryDelay(retryDelay)
                .withPollDelay(pollDelay)
                .build();
    }

    @Bean
    public EventPublisher sourceEventPublisher(
            SourceEventStockHandler sourceEventStockHandler,
            @Value("${source.polling.url}") Resource resource,
            @Value("${source.polling.delay}") int pollDelay,
            @Value("${source.polling.retryDelay}") int retryDelay,
            @Value("${source.polling.maxPoolSize}") int maxPoolSize
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withSourceServiceAdapter()
                .withURI(resource.getURI())
                .withEventHandler(sourceEventStockHandler)
                .withMaxPoolSize(maxPoolSize)
                .withEventRetryDelay(retryDelay)
                .withPollDelay(pollDelay)
                .build();
    }

    @Bean
    public EventPublisher destinationEventPublisher(
            DestinationEventStockHandler destinationEventStockHandler,
            @Value("${destination.polling.url}") Resource resource,
            @Value("${destination.polling.delay}") int pollDelay,
            @Value("${destination.polling.retryDelay}") int retryDelay,
            @Value("${destination.polling.maxPoolSize}") int maxPoolSize
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withDestinationServiceAdapter()
                .withURI(resource.getURI())
                .withEventHandler(destinationEventStockHandler)
                .withMaxPoolSize(maxPoolSize)
                .withEventRetryDelay(retryDelay)
                .withPollDelay(pollDelay)
                .build();
    }

    @Bean
    public EventPublisher depositEventPublisher(
            DepositEventStockHandler depositEventStockHandler,
            @Value("${deposit.polling.url}") Resource resource,
            @Value("${deposit.polling.delay}") int pollDelay,
            @Value("${deposit.polling.retryDelay}") int retryDelay,
            @Value("${deposit.polling.maxPoolSize}") int maxPoolSize
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withDepositServiceAdapter()
                .withURI(resource.getURI())
                .withEventHandler(depositEventStockHandler)
                .withMaxPoolSize(maxPoolSize)
                .withEventRetryDelay(retryDelay)
                .withPollDelay(pollDelay)
                .build();
    }

    @Bean
    public EventPublisher withdrawalSessionEventPublisher(
            WithdrawalSessionEventStockHandler withdrawalSessionEventStockHandler,
            @Value("${withdrawal_session.polling.url}") Resource resource,
            @Value("${withdrawal_session.polling.delay}") int pollDelay,
            @Value("${withdrawal_session.polling.retryDelay}") int retryDelay,
            @Value("${withdrawal_session.polling.maxPoolSize}") int maxPoolSize
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withWithdrawalSessionServiceAdapter()
                .withURI(resource.getURI())
                .withEventHandler(withdrawalSessionEventStockHandler)
                .withMaxPoolSize(maxPoolSize)
                .withEventRetryDelay(retryDelay)
                .withPollDelay(pollDelay)
                .build();
    }

    @Bean
    public EventPublisher rateEventPublisher(
            RateEventStockHandler handler,
            @Value("${rate.polling.url}") Resource resource,
            @Value("${rate.polling.delay}") int pollDelay,
            @Value("${rate.polling.retryDelay}") int retryDelay,
            @Value("${rate.polling.maxPoolSize}") int maxPoolSize
    ) throws IOException {
        return new RatesPollingEventPublisherBuilder()
                .withURI(resource.getURI())
                .withEventHandler(handler)
                .withMaxPoolSize(maxPoolSize)
                .withEventRetryDelay(retryDelay)
                .withPollDelay(pollDelay)
                .build();
    }

}
