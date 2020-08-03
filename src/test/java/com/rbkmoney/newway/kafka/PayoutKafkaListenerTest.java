package com.rbkmoney.newway.kafka;

import com.rbkmoney.newway.poller.listener.PayoutKafkaListener;
import com.rbkmoney.newway.service.PayoutService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.ArgumentMatchers.anyList;

@Slf4j
@ContextConfiguration(classes = {KafkaAutoConfiguration.class, PayoutKafkaListener.class})
public class PayoutKafkaListenerTest extends AbstractKafkaTest {

    @org.springframework.beans.factory.annotation.Value("${kafka.topics.payout.id}")
    public String topic;

    @MockBean
    PayoutService payoutService;

    @Test
    public void listenEmptyChanges() throws InterruptedException {
        sendMessage(topic);
        Mockito.verify(payoutService, Mockito.times(1)).handleEvents(anyList());
    }

}
