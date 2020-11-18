package com.rbkmoney.newway.kafka;

import com.rbkmoney.newway.service.PartyManagementService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@Slf4j
public class PartyManagementKafkaListenerTest extends AbstractKafkaTest {

    @Value("${kafka.topics.party-management.id}")
    public String topic;

    @MockBean
    PartyManagementService partyManagementService;

    @Test
    public void listenEmptyChanges() throws InterruptedException {
        sendMessage(topic);
        verify(partyManagementService, timeout(60000).times(1)).handleEvents(anyList());
    }

}
