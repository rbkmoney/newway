package com.rbkmoney.newway.config;

import com.rbkmoney.newway.poller.listener.InvoicingKafkaListener;
import com.rbkmoney.newway.poller.listener.PartyManagementListener;
import com.rbkmoney.newway.poller.listener.RateListener;
import com.rbkmoney.newway.poller.listener.RecurrentPaymentToolListener;
import com.rbkmoney.newway.service.InvoicingService;
import com.rbkmoney.newway.service.PartyManagementService;
import com.rbkmoney.newway.service.RateService;
import com.rbkmoney.newway.service.RecurrentPaymentToolService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConsumerBeanEnableConfig {

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "false")
    public InvoicingKafkaListener paymentEventsKafkaListener(InvoicingService invoicingService) {
        return new InvoicingKafkaListener(invoicingService);
    }

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "false")
    public RecurrentPaymentToolListener recurrentPaymentToolListener(RecurrentPaymentToolService recurrentPaymentToolService) {
        return new RecurrentPaymentToolListener(recurrentPaymentToolService);
    }

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "false")
    public PartyManagementListener partyManagementListener(PartyManagementService partyManagementService) {
        return new PartyManagementListener(partyManagementService);
    }

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "false")
    public RateListener rateListener(RateService rateService) {
        return new RateListener(rateService);
    }

}
