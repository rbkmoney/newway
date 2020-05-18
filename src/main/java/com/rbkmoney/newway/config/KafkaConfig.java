package com.rbkmoney.newway.config;

import com.rbkmoney.kafka.common.exception.handler.SeekToCurrentWithSleepBatchErrorHandler;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.config.properties.KafkaSslProperties;
import com.rbkmoney.newway.serde.RateSinkEventDeserializer;
import com.rbkmoney.newway.serde.SinkEventDeserializer;
import com.rbkmoney.xrates.rate.SinkEvent;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.BatchErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(KafkaSslProperties.class)
public class KafkaConfig {

    @Value("${kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;
    @Value("${kafka.consumer.enable-auto-commit}")
    private boolean enableAutoCommit;
    @Value("${kafka.consumer.group-id}")
    private String groupId;
    @Value("${kafka.topics.party-management.consumer.group-id}")
    private String partyConsumerGroup;
    @Value("${kafka.client-id}")
    private String clientId;
    @Value("${kafka.consumer.max-poll-records}")
    private int maxPollRecords;
    @Value("${kafka.consumer.max-poll-interval-ms}")
    private int maxPollIntervalMs;
    @Value("${kafka.consumer.session-timeout-ms}")
    private int sessionTimeoutMs;

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${kafka.consumer.invoicing.concurrency}")
    private int concurrency;
    @Value("${kafka.consumer.recurrent-payment-tool.concurrency}")
    private int recPayToolConcurrency;
    @Value("${kafka.consumer.party-management.concurrency}")
    private int partyConcurrency;
    @Value("${kafka.consumer.rate.concurrency}")
    private int rateConcurrency;
    @Value("${retry-policy.maxAttempts}")
    int maxAttempts;

    @Bean
    public Map<String, Object> consumerConfigs(KafkaSslProperties kafkaSslProperties) {
        return createConsumerConfig(kafkaSslProperties, SinkEventDeserializer.class);
    }

    @Bean
    public Map<String, Object> consumerRateConfigs(KafkaSslProperties kafkaSslProperties) {
        return createConsumerConfig(kafkaSslProperties, RateSinkEventDeserializer.class);
    }

    private <T> Map<String, Object> createConsumerConfig(KafkaSslProperties kafkaSslProperties, Class<T> clazz) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, clazz);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
        configureSsl(props, kafkaSslProperties);
        return props;
    }

    private void configureSsl(Map<String, Object> props, KafkaSslProperties kafkaSslProperties) {
        if (kafkaSslProperties.isEnabled()) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SSL.name());
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, new File(kafkaSslProperties.getTrustStoreLocation()).getAbsolutePath());
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaSslProperties.getTrustStorePassword());
            props.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, kafkaSslProperties.getKeyStoreType());
            props.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, kafkaSslProperties.getTrustStoreType());
            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, new File(kafkaSslProperties.getKeyStoreLocation()).getAbsolutePath());
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, kafkaSslProperties.getKeyStorePassword());
            props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, kafkaSslProperties.getKeyPassword());
        }
    }

    @Bean
    public ConsumerFactory<String, MachineEvent> consumerFactory(KafkaSslProperties kafkaSslProperties) {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(kafkaSslProperties));
    }

    @Bean
    public ConsumerFactory<String, SinkEvent> consumerRateFactory(KafkaSslProperties kafkaSslProperties) {
        return new DefaultKafkaConsumerFactory<>(consumerRateConfigs(kafkaSslProperties));
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, MachineEvent>> kafkaListenerContainerFactory(
            ConsumerFactory<String, MachineEvent> consumerFactory) {
        return createConcurrentFactory(consumerFactory, concurrency);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, MachineEvent>> recPayToolContainerFactory(
            ConsumerFactory<String, MachineEvent> consumerFactory) {
        return createConcurrentFactory(consumerFactory, recPayToolConcurrency);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SinkEvent>> rateContainerFactory(
            ConsumerFactory<String, SinkEvent> consumerRateFactory) {
        return createRateConcurrentFactory(consumerRateFactory, rateConcurrency);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, MachineEvent>> partyManagementContainerFactory(
            KafkaSslProperties kafkaSslProperties) {
        Map<String, Object> configs = consumerConfigs(kafkaSslProperties);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, partyConsumerGroup);
        ConsumerFactory<String, MachineEvent> consumerFactory = new DefaultKafkaConsumerFactory<>(configs);
        return createConcurrentFactory(consumerFactory, partyConcurrency);
    }

    private KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, MachineEvent>> createConcurrentFactory(
            ConsumerFactory<String, MachineEvent> consumerFactory, int threadsNumber) {
        ConcurrentKafkaListenerContainerFactory<String, MachineEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        initFactory(consumerFactory, threadsNumber, factory);
        return factory;
    }

    private KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SinkEvent>> createRateConcurrentFactory(
            ConsumerFactory<String, SinkEvent> consumerFactory, int threadsNumber) {
        ConcurrentKafkaListenerContainerFactory<String, SinkEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        initFactory(consumerFactory, threadsNumber, factory);
        return factory;
    }

    private <T> void initFactory(ConsumerFactory<String, T> consumerFactory, int threadsNumber, ConcurrentKafkaListenerContainerFactory<String, T> factory) {
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckOnError(false);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setBatchErrorHandler(kafkaErrorHandler());
        factory.setConcurrency(threadsNumber);
    }

    public BatchErrorHandler kafkaErrorHandler() {
        return new SeekToCurrentWithSleepBatchErrorHandler();
    }

}
