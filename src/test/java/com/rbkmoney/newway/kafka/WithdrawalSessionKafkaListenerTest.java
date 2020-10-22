package com.rbkmoney.newway.kafka;

import com.rbkmoney.newway.poller.listener.WithdrawalSessionKafkaListener;
import com.rbkmoney.newway.service.WithdrawalSessionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.ArgumentMatchers.anyList;

@Slf4j
public class
WithdrawalSessionKafkaListenerTest extends AbstractKafkaTest {

    @org.springframework.beans.factory.annotation.Value("${kafka.topics.withdrawal-session.id}")
    public String topic;

    @MockBean
    WithdrawalSessionService service;

    @Test
    public void listenEmptyChanges() throws InterruptedException {
        sendMessage(topic);
        Mockito.verify(service, Mockito.times(1)).handleEvents(anyList());
    }

}
