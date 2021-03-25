package com.rbkmoney.newway.kafka;

import com.rbkmoney.newway.config.properties.KafkaConsumerProperties;
import com.rbkmoney.newway.config.properties.KafkaSslProperties;
import com.rbkmoney.newway.serde.SinkEventDeserializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@TestConfiguration
@RequiredArgsConstructor
public class KafkaConfigTest {

    private static final String PKCS_12 = "PKCS12";
    private final KafkaConsumerProperties kafkaConsumerProperties;

    @Bean
    @Primary
    public Map<String, Object> consumerConfigs(KafkaSslProperties kafkaSslProperties) {
        return createConsumerConfig(kafkaSslProperties);
    }

    private Map<String, Object> createConsumerConfig(KafkaSslProperties kafkaSslProperties) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "dev-kafka-mirror.bst1.rbkmoney.net:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SinkEventDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "kstruzhkin-test");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaConsumerProperties.isEnableAutoCommit());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConsumerProperties.getAutoOffsetReset());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaConsumerProperties.getMaxPollRecords());
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaConsumerProperties.getSessionTimeoutMs());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaConsumerProperties.getMaxPollIntervalMs());
        configureSsl(props, kafkaSslProperties);
        return props;
    }

    private void configureSsl(Map<String, Object> props, KafkaSslProperties kafkaSslProperties) {
        if (kafkaSslProperties.isEnabled()) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
                    new File("src/test/resources/truststore.p12").getAbsolutePath());
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "x");
            props.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, PKCS_12);
            props.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, PKCS_12);
            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG,
                    new File("src/test/resources/kstruzhkin.p12").getAbsolutePath());
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "x");
            props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "x");
        }
    }

}
