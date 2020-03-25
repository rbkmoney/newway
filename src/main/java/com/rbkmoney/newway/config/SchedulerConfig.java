package com.rbkmoney.newway.config;

import com.rbkmoney.damsel.domain_config.RepositorySrv;
import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolEventSinkSrv;
import com.rbkmoney.newway.poller.dominant.DominantPoller;
import com.rbkmoney.newway.poller.dominant.TemporaryDominantPoller;
import com.rbkmoney.newway.poller.event_stock.RecurrentPaymentToolPoller;
import com.rbkmoney.newway.service.DominantService;
import com.rbkmoney.newway.service.RecurrentPaymentToolService;
import com.rbkmoney.newway.service.TemporaryDominantService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulerConfig {

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "true")
    public DominantPoller dominantPoller(RepositorySrv.Iface dominantClient,
                                         DominantService dominantService,
                                         @Value("${dmt.polling.maxQuerySize}") int maxQuerySize,
                                         @Value("${dmt.polling.enabled}") boolean pollingEnabled){
        return new DominantPoller(dominantClient, dominantService, maxQuerySize, pollingEnabled);
    }

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "true")
    public TemporaryDominantPoller temporaryDominantPoller(RepositorySrv.Iface dominantClient,
                                                  TemporaryDominantService dominantService,
                                                  @Value("${dmt.polling.maxQuerySize}") int maxQuerySize,
                                                  @Value("${dmt.polling.enabled}") boolean pollingEnabled){
        return new TemporaryDominantPoller(dominantClient, dominantService, maxQuerySize, pollingEnabled);
    }

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "true")
    public RecurrentPaymentToolPoller recurrentPaymentToolPoller(RecurrentPaymentToolEventSinkSrv.Iface recPayToolClient,
                                                                 RecurrentPaymentToolService recurrentPaymentToolService,
                                                                 @Value("${recurrentPaymentTool.polling.limit}") int limit,
                                                                 @Value("${recurrentPaymentTool.polling.enabled}") boolean pollingEnabled){
        return new RecurrentPaymentToolPoller(recPayToolClient, recurrentPaymentToolService, limit, pollingEnabled);
    }
}
