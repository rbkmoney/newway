package com.rbkmoney.newway.kafka;

import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.newway.poller.listener.InvoicingKafkaListener;
import com.rbkmoney.newway.service.InvoicingService;
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
@ContextConfiguration(classes = {KafkaAutoConfiguration.class, InvoicingKafkaListener.class})
public class InvoicingKafkaListenerTest extends AbstractKafkaTest {

    @org.springframework.beans.factory.annotation.Value("${kafka.topics.invoice.id}")
    public String topic;

    @MockBean
    InvoicingService invoicingService;

    @Test
    public void listenEmptyChanges() throws InterruptedException {
        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setEvent(createMessage());

        writeToTopic(topic, sinkEvent);

        waitForTopicSync();

        Mockito.verify(invoicingService, Mockito.times(1)).handleEvents(anyList());
    }

    private MachineEvent createMessage() {
        MachineEvent message = new MachineEvent();
        Value data = new Value();
        data.setBin(new byte[0]);
        message.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        message.setEventId(1L);
        message.setSourceNs("sad");
        message.setSourceId("sda");
        message.setData(data);
        return message;
    }

}
