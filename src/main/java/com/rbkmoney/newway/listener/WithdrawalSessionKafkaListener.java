package com.rbkmoney.newway.listener;

import com.rbkmoney.kafka.common.util.LogUtil;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.newway.service.WithdrawalSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class WithdrawalSessionKafkaListener {

    private final WithdrawalSessionService withdrawalSessionService;

    @KafkaListener(topics = "${kafka.topics.withdrawal-session.id}",
            containerFactory = "withdrawalSessionContainerFactory")
    public void handle(List<ConsumerRecord<String, SinkEvent>> messages, Acknowledgment ack) {
        log.info("Got machineEvent batch with size: {}", messages.size());
        if (!CollectionUtils.isEmpty(messages)) {
            withdrawalSessionService.handleEvents(messages.stream()
                    .map(m -> m.value().getEvent())
                    .collect(Collectors.toList()));
            ack.acknowledge();
        }
        log.info("Batch has been committed, size={}, {}", messages.size(),
                LogUtil.toSummaryStringWithSinkEventValues(messages));
    }
}
