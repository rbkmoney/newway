package com.rbkmoney.newway.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kafka.consumer")
public class KafkaConsumerProperties {

    private String autoOffsetReset;
    private boolean enableAutoCommit;
    private String groupId;
    private int maxPollRecords;
    private int maxPollIntervalMs;
    private int sessionTimeoutMs;
    private int invoicingConcurrency;
    private int recurrentPaymentToolConcurrency;
    private int partyManagementConcurrency;
    private int rateConcurrency;
    private int depositConcurrency;
    private int identityConcurrency;
    private int walletConcurrency;
    private int withdrawalConcurrency;
    private int payoutConcurrency;
    private int sourceConcurrency;
    private int destinationConcurrency;
    private int withdrawalSessionConcurrency;

}
