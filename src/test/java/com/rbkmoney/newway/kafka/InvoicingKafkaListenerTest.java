package com.rbkmoney.newway.kafka;

import com.rbkmoney.newway.service.InvoicingService;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyList;

public class InvoicingKafkaListenerTest extends AbstractKafkaTest {

    @org.springframework.beans.factory.annotation.Value("${kafka.topics.invoice.id}")
    public String topic;

    @MockBean
    InvoicingService invoicingService;

    @Test
    public void listenEmptyChanges() {
        sendMessage(topic);
        Mockito.verify(invoicingService, Mockito.timeout(TimeUnit.MINUTES.toMillis(1)).times(1))
                .handleEvents(anyList());
    }

}
