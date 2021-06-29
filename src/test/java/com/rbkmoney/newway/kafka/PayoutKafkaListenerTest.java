package com.rbkmoney.newway.kafka;

import com.rbkmoney.newway.service.PayoutService;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyList;

public class PayoutKafkaListenerTest extends AbstractKafkaTest {

    @org.springframework.beans.factory.annotation.Value("${kafka.topics.pm-events-payout.id}")
    public String topic;

    @MockBean
    PayoutService payoutService;

    @Test
    public void listenEmptyChanges() {
        sendMessage(topic);
        Mockito.verify(payoutService, Mockito.timeout(TimeUnit.MINUTES.toMillis(1)).times(1))
                .handleEvents(anyList());
    }

}
