package com.rbkmoney.newway.kafka;

import com.rbkmoney.newway.AbstractTestUtils;
import com.rbkmoney.newway.NewwayApplication;
import com.rbkmoney.newway.TestContainers;
import com.rbkmoney.newway.TestContainersBuilder;
import com.rbkmoney.newway.utils.NewwayTestPropertyValuesBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = NewwayApplication.class, initializers = AbstractKafkaTest.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractKafkaTest extends AbstractTestUtils {

    private static TestContainers testContainers = TestContainersBuilder.builder(false)
            .addPostgreSQLTestContainer()
            .build();

    public static final String SOURCE_ID = "source_id";
    public static final String SOURCE_NS = "source_ns";

    private static final String CONFLUENT_PLATFORM_VERSION = "5.0.1";

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(CONFLUENT_PLATFORM_VERSION).withEmbeddedZookeeper();

    @BeforeClass
    public static void beforeClass() {
        testContainers.startTestContainers();
    }

    @AfterClass
    public static void afterClass() {
        testContainers.stopTestContainers();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            NewwayTestPropertyValuesBuilder
                    .build(testContainers)
                    .and("kafka.bootstrap-servers=" + kafka.getBootstrapServers(),
                            "kafka.ssl.enabled=false",
                            "kafka.consumer.group-id=TestListener",
                            "kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
                            "kafka.consumer.value-deserializer=com.rbkmoney.newway.serde.SinkEventDeserializer",
                            "kafka.consumer.enable-auto-commit=false",
                            "kafka.consumer.auto-offset-reset=earliest",
                            "kafka.consumer.client-id=test",
                            "kafka.listener.type=batch",
                            "kafka.listener.ack-mode=manual",
                            "kafka.listener.concurrency=1",
                            "kafka.listener.poll-timeout=1000",
                            "kafka.listener.no-poll-threshold=5.0",
                            "kafka.listener.log-container-config=true",
                            "kafka.listener.monitor-interval=10s",
                            "kafka.client-id=test",
                            "kafka.topics.invoicing=test-topic")
                    .applyTo(configurableApplicationContext);
        }
    }
}
