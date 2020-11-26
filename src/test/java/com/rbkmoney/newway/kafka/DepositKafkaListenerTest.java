package com.rbkmoney.newway.kafka;

import com.rbkmoney.newway.service.DepositService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyList;

@Slf4j
public class DepositKafkaListenerTest extends AbstractKafkaTest {

    @org.springframework.beans.factory.annotation.Value("${kafka.topics.deposit.id}")
    public String topic;

    @MockBean
    DepositService depositService;

    @Test
    public void listenEmptyChanges() {
        sendMessage(topic);
        Mockito.verify(depositService, Mockito.timeout(TimeUnit.MINUTES.toMillis(1)).times(1))
                .handleEvents(anyList());
    }

}
