package com.rbkmoney.newway.kafka;

import com.rbkmoney.newway.service.PartyManagementService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class PartyManagementKafkaListenerTest extends AbstractKafkaTest {

    @Value("${kafka.topics.party-management.id}")
    public String topic;

    @MockBean
    PartyManagementService partyManagementService;

    @Test
    public void listenEmptyChanges() {
        sendMessage(topic);
        verify(partyManagementService, timeout(TimeUnit.MINUTES.toMillis(1)).times(1))
                .handleEvents(anyList());
    }

}
