package com.rbkmoney.newway.config;

import com.rbkmoney.newway.poller.listener.*;
import com.rbkmoney.newway.service.*;
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
    public RecurrentPaymentToolListener recurrentPaymentToolListener(
            RecurrentPaymentToolService recurrentPaymentToolService) {
        return new RecurrentPaymentToolListener(recurrentPaymentToolService);
    }

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "false")
    public PartyManagementListener partyManagementListener(PartyManagementService partyManagementService) {
        return new PartyManagementListener(partyManagementService);
    }

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "false")
    public DepositKafkaListener depositKafkaListener(DepositService depositService) {
        return new DepositKafkaListener(depositService);
    }

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "false")
    public DestinationKafkaListener destinationKafkaListener(DestinationService destinationService) {
        return new DestinationKafkaListener(destinationService);
    }

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "false")
    public IdentityKafkaListener identityKafkaListener(IdentityService identityService) {
        return new IdentityKafkaListener(identityService);
    }

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "false")
    public SourceKafkaListener sourceKafkaListener(SourceService sourceService) {
        return new SourceKafkaListener(sourceService);
    }

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "false")
    public WalletKafkaListener walletKafkaListener(WalletService walletService) {
        return new WalletKafkaListener(walletService);
    }

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "false")
    public WithdrawalKafkaListener withdrawalKafkaListener(WithdrawalService withdrawalService) {
        return new WithdrawalKafkaListener(withdrawalService);
    }

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "false")
    public WithdrawalSessionKafkaListener withdrawalSessionKafkaListener(
            WithdrawalSessionService withdrawalSessionService) {
        return new WithdrawalSessionKafkaListener(withdrawalSessionService);
    }

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "false")
    public PayoutKafkaListener payoutSessionKafkaListener(PayoutService payoutService) {
        return new PayoutKafkaListener(payoutService);
    }

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "false")
    public RateListener rateKafkaListener(RateService rateService) {
        return new RateListener(rateService);
    }

}
