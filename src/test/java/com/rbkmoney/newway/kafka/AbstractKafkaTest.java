package com.rbkmoney.newway.kafka;

import com.rbkmoney.easyway.*;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.newway.NewwayApplication;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = NewwayApplication.class, initializers = AbstractKafkaTest.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractKafkaTest extends AbstractTestUtils {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    private static TestContainers testContainers = TestContainersBuilder.builderWithTestContainers(getTestContainersParametersSupplier())
            .addKafkaTestContainer()
            .addPostgresqlTestContainer()
            .build();

    @BeforeClass
    public static void beforeClass() {
        testContainers.getKafkaTestContainer().ifPresent(kafkaContainer -> kafkaContainer.withStartupTimeout(Duration.ofMinutes(10)));
        testContainers.startTestContainers();
    }

    @AfterClass
    public static void afterClass() {
        testContainers.stopTestContainers();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(testContainers.getEnvironmentProperties(getEnvironmentPropertiesConsumer())).
                    applyTo(configurableApplicationContext);
        }
    }

    private static Supplier<TestContainersParameters> getTestContainersParametersSupplier() {
        return () -> {
            TestContainersParameters testContainersParameters = new TestContainersParameters();
            testContainersParameters.setPostgresqlJdbcUrl("jdbc:postgresql://localhost:5432/newway");

            return testContainersParameters;
        };
    }

    private static Consumer<EnvironmentProperties> getEnvironmentPropertiesConsumer() {
        return environmentProperties -> {
            environmentProperties.put("info.single-instance-mode", "false");
            environmentProperties.put("bm.polling.enabled", "false");
            environmentProperties.put("dmt.polling.enabled", "false");
            environmentProperties.put("recurrentPaymentTool.polling.enabled", "false");
        };
    }

    protected void waitForTopicSync() throws InterruptedException {
        Thread.sleep(10000L);
    }

    protected void writeToTopic(String topic, SinkEvent sinkEvent) {
        Producer<String, SinkEvent> producer = createProducer();
        ProducerRecord<String, SinkEvent> producerRecord = new ProducerRecord<>(topic, null, sinkEvent);
        try {
            producer.send(producerRecord).get();
        } catch (Exception e) {
            log.error("KafkaAbstractTest initialize e: ", e);
        }
        producer.close();
    }

    private Producer<String, SinkEvent> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "client_id");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, new ThriftSerializer<SinkEvent>().getClass());
        return new KafkaProducer<>(props);
    }

    protected MachineEvent createMessage() {
        MachineEvent message = new MachineEvent();
        com.rbkmoney.machinegun.msgpack.Value data = new com.rbkmoney.machinegun.msgpack.Value();
        data.setBin(new byte[0]);
        message.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        message.setEventId(1L);
        message.setSourceNs("sad");
        message.setSourceId("sda");
        message.setData(data);
        return message;
    }

    protected void sendMessage(String topic) throws InterruptedException {
        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setEvent(createMessage());

        writeToTopic(topic, sinkEvent);

        waitForTopicSync();
    }

}
