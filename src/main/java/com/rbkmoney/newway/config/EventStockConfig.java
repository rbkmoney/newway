package com.rbkmoney.newway.config;

import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.poll.FistfulServiceAdapter;
import com.rbkmoney.eventstock.client.poll.PollingEventPublisherBuilder;
import com.rbkmoney.eventstock.client.poll.ServiceAdapter;
import com.rbkmoney.newway.poller.event_stock.*;
import com.rbkmoney.newway.poller.event_stock.impl.wallet.AbstractWalletHandler;
import com.rbkmoney.woody.api.ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class EventStockConfig {

    @Bean
    public EventPublisher partyManagementEventPublisher(
            PartyManagementEventStockHandler partyManagementEventStockHandler,
            @Value("${bm.partyManagement.url}") Resource resource,
            @Value("${bm.partyManagement.polling.delay}") int pollDelay,
            @Value("${bm.partyManagement.polling.retryDelay}") int retryDelay,
            @Value("${bm.partyManagement.polling.maxPoolSize}") int maxPoolSize,
            @Value("${bm.partyManagement.polling.maxQuerySize}") int maxQuerySize
    ) throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(resource.getURI())
                .withEventHandler(partyManagementEventStockHandler)
                .withMaxPoolSize(maxPoolSize)
                .withEventRetryDelay(retryDelay)
                .withPollDelay(pollDelay)
                .withMaxQuerySize(maxQuerySize)
                .build();
    }

    @Bean
    public EventPublisher invoicingEventPublisher(
            InvoicingEventStockHandler invoicingEventStockHandler,
            @Value("${bm.invoicing.url}") Resource resource,
            @Value("${bm.invoicing.polling.delay}") int pollDelay,
            @Value("${bm.invoicing.polling.retryDelay}") int retryDelay,
            @Value("${bm.invoicing.polling.maxPoolSize}") int maxPoolSize,
            @Value("${bm.invoicing.polling.maxQuerySize}") int maxQuerySize
    ) throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(resource.getURI())
                .withEventHandler(invoicingEventStockHandler)
                .withMaxPoolSize(maxPoolSize)
                .withEventRetryDelay(retryDelay)
                .withPollDelay(pollDelay)
                .withMaxQuerySize(maxQuerySize)
                .build();
    }

    @Bean
    public EventPublisher payoutEventPublisher(
            PayoutEventStockHandler payoutEventStockHandler,
            @Value("${bm.payout.url}") Resource resource,
            @Value("${bm.payout.polling.delay}") int pollDelay,
            @Value("${bm.payout.polling.retryDelay}") int retryDelay,
            @Value("${bm.payout.polling.maxPoolSize}") int maxPoolSize
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
        return new PollingEventPublisherBuilder() {
            @Override
            protected ServiceAdapter createServiceAdapter(ClientBuilder clientBuilder) {
                return FistfulServiceAdapter.buildWalletAdapter(clientBuilder);
            }
        }
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
        return new PollingEventPublisherBuilder() {
            @Override
            protected ServiceAdapter createServiceAdapter(ClientBuilder clientBuilder) {
                return FistfulServiceAdapter.buildIdentityAdapter(clientBuilder);
            }
        }
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
        return new PollingEventPublisherBuilder() {
            @Override
            protected ServiceAdapter createServiceAdapter(ClientBuilder clientBuilder) {
                return FistfulServiceAdapter.buildWithdrawalAdapter(clientBuilder);
            }
        }
                .withURI(resource.getURI())
                .withEventHandler(withdrawalEventStockHandler)
                .withMaxPoolSize(maxPoolSize)
                .withEventRetryDelay(retryDelay)
                .withPollDelay(pollDelay)
                .build();
    }

}
