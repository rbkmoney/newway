package com.rbkmoney.newway.config;

import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.poll.PollingEventPublisherBuilder;
import com.rbkmoney.newway.poller.handler.InvoicingEventStockHandler;
import com.rbkmoney.newway.poller.handler.PayoutEventStockHandler;
import com.rbkmoney.newway.poller.handler.PartyManagementEventStockHandler;
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
            @Value("${bm.processing.url}") Resource resource,
            @Value("${bm.processing.polling.delay}") int pollDelay,
            @Value("${bm.processing.polling.retryDelay}") int retryDelay,
            @Value("${bm.processing.polling.maxPoolSize}") int maxPoolSize
    ) throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(resource.getURI())
                .withEventHandler(partyManagementEventStockHandler)
                .withMaxPoolSize(maxPoolSize)
                .withEventRetryDelay(retryDelay)
                .withPollDelay(pollDelay)
                .build();
    }

    @Bean
    public EventPublisher invoicingEventPublisher(
            InvoicingEventStockHandler invoicingEventStockHandler,
            @Value("${bm.processing.url}") Resource resource,
            @Value("${bm.processing.polling.delay}") int pollDelay,
            @Value("${bm.processing.polling.retryDelay}") int retryDelay,
            @Value("${bm.processing.polling.maxPoolSize}") int maxPoolSize
    ) throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(resource.getURI())
                .withEventHandler(invoicingEventStockHandler)
                .withMaxPoolSize(maxPoolSize)
                .withEventRetryDelay(retryDelay)
                .withPollDelay(pollDelay)
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

}
