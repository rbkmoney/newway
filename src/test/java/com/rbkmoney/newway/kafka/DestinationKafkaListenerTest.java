package com.rbkmoney.newway.kafka;

import com.rbkmoney.newway.service.DestinationService;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyList;

public class DestinationKafkaListenerTest extends AbstractKafkaTest {

    @org.springframework.beans.factory.annotation.Value("${kafka.topics.destination.id}")
    public String topic;

    @MockBean
    DestinationService destinationService;

    @Test
    public void listenEmptyChanges() {
        sendMessage(topic);
        Mockito.verify(destinationService, Mockito.timeout(TimeUnit.MINUTES.toMillis(1)).times(1))
                .handleEvents(anyList());
    }

}
