package com.rbkmoney.newway.kafka;

import com.rbkmoney.newway.poller.listener.WithdrawalKafkaListener;
import com.rbkmoney.newway.service.WithdrawalService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.ArgumentMatchers.anyList;

@Slf4j
public class WithdrawalKafkaListenerTest extends AbstractKafkaTest {

    @org.springframework.beans.factory.annotation.Value("${kafka.topics.withdrawal.id}")
    public String topic;

    @MockBean
    WithdrawalService service;

    @Test
    public void listenEmptyChanges() throws InterruptedException {
        sendMessage(topic);
        Mockito.verify(service, Mockito.times(1)).handleEvents(anyList());
    }

}
