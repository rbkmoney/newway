package com.rbkmoney.newway.kafka;

import com.rbkmoney.newway.service.WithdrawalSessionService;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyList;

public class
WithdrawalSessionKafkaListenerTest extends AbstractKafkaTest {

    @org.springframework.beans.factory.annotation.Value("${kafka.topics.withdrawal-session.id}")
    public String topic;

    @MockBean
    WithdrawalSessionService service;

    @Test
    public void listenEmptyChanges() {
        sendMessage(topic);
        Mockito.verify(service, Mockito.timeout(TimeUnit.MINUTES.toMillis(1)).times(1))
                .handleEvents(anyList());
    }

}
