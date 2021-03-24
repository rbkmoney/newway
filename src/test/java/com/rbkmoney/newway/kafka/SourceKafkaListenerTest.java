package com.rbkmoney.newway.kafka;

import com.rbkmoney.newway.service.SourceService;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyList;

public class SourceKafkaListenerTest extends AbstractKafkaTest {

    @org.springframework.beans.factory.annotation.Value("${kafka.topics.source.id}")
    public String topic;

    @MockBean
    SourceService sourceService;

    @Test
    public void listenEmptyChanges() {
        sendMessage(topic);
        Mockito.verify(sourceService, Mockito.timeout(TimeUnit.MINUTES.toMillis(1)).times(1))
                .handleEvents(anyList());
    }

}
