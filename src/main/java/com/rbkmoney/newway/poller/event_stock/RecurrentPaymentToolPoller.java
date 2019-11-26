package com.rbkmoney.newway.poller.event_stock;

import com.rbkmoney.damsel.payment_processing.EventRange;
import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolEvent;
import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolEventSinkSrv;
import com.rbkmoney.newway.service.RecurrentPaymentToolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@DependsOn("flywayInitializer")
public class RecurrentPaymentToolPoller {

    private final RecurrentPaymentToolEventSinkSrv.Iface recurrentPaymentToolClient;
    private final RecurrentPaymentToolService recurrentPaymentToolService;

    @Value("${recurrentPaymentTool.polling.limit}")
    private int limit;
    private long after;

    @PostConstruct
    public void afterPropertieSet(){
        after = recurrentPaymentToolService.getLastEventId().orElse(0L);
    }

    @Scheduled(fixedDelayString = "${recurrentPaymentTool.polling.delay}")
    public void process() {
        try {
            List<RecurrentPaymentToolEvent> events = recurrentPaymentToolClient.getEvents(getEventRange());
            events.forEach(event -> {
                try {
                    recurrentPaymentToolService.handleEvents(event, event);
                    after = event.getId();
                } catch (RuntimeException ex) {
                    throw new RuntimeException(String.format("Unexpected error when polling recurrent payment tool eventSink, eventId=%d", after), ex);
                }
            });
        } catch (TException e) {
            log.warn("Error to polling recurrent payment tool eventSink, after={}", after, e);
        }
    }

    private EventRange getEventRange() {
        return new EventRange().setAfter(after).setLimit(limit);
    }
}
