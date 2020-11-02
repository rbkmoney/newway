package com.rbkmoney.newway.kafka;

import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.newway.poller.listener.InvoicingKafkaListener;
import com.rbkmoney.newway.poller.listener.SourceKafkaListener;
import com.rbkmoney.newway.service.InvoicingService;
import com.rbkmoney.newway.service.SourceService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.anyList;

@Slf4j
public class SourceKafkaListenerTest extends AbstractKafkaTest {

    @org.springframework.beans.factory.annotation.Value("${kafka.topics.source.id}")
    public String topic;

    @MockBean
    SourceService sourceService;

    @Test
    public void listenEmptyChanges() throws InterruptedException {
        sendMessage(topic);
        Mockito.verify(sourceService, Mockito.times(1)).handleEvents(anyList());
    }

}
