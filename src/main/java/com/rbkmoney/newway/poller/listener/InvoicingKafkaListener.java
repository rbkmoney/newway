package com.rbkmoney.newway.poller.listener;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.newway.service.InvoicingService;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

@Slf4j
@RequiredArgsConstructor
public class InvoicingKafkaListener {

    private final InvoicingService invoicingService;
    private final MachineEventParser<EventPayload> parser;

    @KafkaListener(topics = "${kafka.topics.invoice.id}", containerFactory = "kafkaListenerContainerFactory")
    public void handle(SinkEvent sinkEvent, Acknowledgment ack) {
        log.debug("Reading sinkEvent, sourceId:{}, eventId:{}", sinkEvent.getEvent().getSourceId(), sinkEvent.getEvent().getEventId());
        EventPayload payload = parser.parse(sinkEvent.getEvent());
        if (payload.isSetInvoiceChanges()) {
            invoicingService.handleEvents(sinkEvent.getEvent(), payload);
        }
        ack.acknowledge();
    }
}