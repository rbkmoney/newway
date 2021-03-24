package com.rbkmoney.newway.config;

import com.rbkmoney.damsel.domain_config.RepositorySrv;
import com.rbkmoney.newway.poller.dominant.DominantPoller;
import com.rbkmoney.newway.service.DominantService;
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
                                         @Value("${dmt.polling.enabled}") boolean pollingEnabled) {
        return new DominantPoller(dominantClient, dominantService, maxQuerySize, pollingEnabled);
    }
}
