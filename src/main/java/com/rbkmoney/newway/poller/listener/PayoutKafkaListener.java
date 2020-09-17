package com.rbkmoney.newway.poller.listener;

import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.newway.service.PayoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class PayoutKafkaListener {

    private final PayoutService payoutService;

    @KafkaListener(topics = "${kafka.topics.payout.id}", containerFactory = "payoutContainerFactory")
    public void handle(List<ConsumerRecord<String, Event>> messages, Acknowledgment ack) {
        log.info("Got machineEvent batch with size: {}", messages.size());
        payoutService.handleEvents(messages.stream()
                .map(ConsumerRecord::value)
                .collect(Collectors.toList()));
        ack.acknowledge();
        log.info("Batch has been committed, size={}", messages.size());
    }
}