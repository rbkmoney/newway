package com.rbkmoney.newway.config;

import com.rbkmoney.damsel.domain_config.RepositorySrv;
import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolEventSinkSrv;
import com.rbkmoney.damsel.payout_processing.EventSinkSrv;
import com.rbkmoney.newway.domain.Nw;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.jooq.Schema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class ApplicationConfig {

    @Bean
    public RepositorySrv.Iface dominantClient(@Value("${dmt.url}") Resource resource,
                                              @Value("${dmt.networkTimeout}") int networkTimeout) throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI())
                .build(RepositorySrv.Iface.class);
    }

    @Bean
    public RecurrentPaymentToolEventSinkSrv.Iface recurrentPaymentToolClient(@Value("${recurrentPaymentTool.url}") Resource resource,
                                                                             @Value("${recurrentPaymentTool.networkTimeout}") int networkTimeout) throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI())
                .build(RecurrentPaymentToolEventSinkSrv.Iface.class);
    }

    @Bean
    public EventSinkSrv.Iface payouterClient(
            @Value("${payouter.url}") Resource resource,
            @Value("${payouter.networkTimeout}") int networkTimeout
    ) throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI())
                .build(EventSinkSrv.Iface.class);
    }

    @Bean
    public Schema schema() {
        return Nw.NW;
    }

}
