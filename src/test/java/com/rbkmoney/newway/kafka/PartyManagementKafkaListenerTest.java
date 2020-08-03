package com.rbkmoney.newway.kafka;

import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.newway.config.KafkaConsumerBeanEnableConfig;
import com.rbkmoney.newway.poller.listener.InvoicingKafkaListener;
import com.rbkmoney.newway.poller.listener.PartyManagementListener;
import com.rbkmoney.newway.service.PartyManagementService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@ContextConfiguration(classes = {KafkaAutoConfiguration.class, KafkaConsumerBeanEnableConfig.class})
public class PartyManagementKafkaListenerTest extends AbstractKafkaTest {

    @org.springframework.beans.factory.annotation.Value("${kafka.topics.party-management.id}")
    public String topic;

    @MockBean
    PartyManagementService partyManagementService;

    @Test
    public void listenEmptyChanges() throws InterruptedException {
        sendMessage(topic);
        verify(partyManagementService, times(1)).handleEvents(anyList());
    }

}
