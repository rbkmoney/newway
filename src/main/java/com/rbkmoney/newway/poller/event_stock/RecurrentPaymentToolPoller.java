package com.rbkmoney.newway.poller.event_stock;

import com.rbkmoney.damsel.payment_processing.EventRange;
import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolEvent;
import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolEventSinkSrv;
import com.rbkmoney.newway.service.RecurrentPaymentToolService;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@DependsOn("flywayInitializer")
public class RecurrentPaymentToolPoller {

    private final RecurrentPaymentToolEventSinkSrv.Iface recurrentPaymentToolClient;
    private final RecurrentPaymentToolService recurrentPaymentToolService;
    private final int limit;
    private final boolean pollingEnabled;
    private long after;

    public RecurrentPaymentToolPoller(RecurrentPaymentToolEventSinkSrv.Iface recurrentPaymentToolClient,
                                      RecurrentPaymentToolService recurrentPaymentToolService,
                                      int limit,
                                      boolean pollingEnabled) {
        this.recurrentPaymentToolClient = recurrentPaymentToolClient;
        this.recurrentPaymentToolService = recurrentPaymentToolService;
        this.limit = limit;
        this.after = recurrentPaymentToolService.getLastEventId().orElse(0L);
        this.pollingEnabled = pollingEnabled;
    }

    @PostConstruct
    public void afterPropertieSet(){
        after = recurrentPaymentToolService.getLastEventId().orElse(0L);
    }

    @Scheduled(fixedDelayString = "${recurrentPaymentTool.polling.delay}")
    public void process() {
        if (pollingEnabled) {
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
    }

    private EventRange getEventRange() {
        return new EventRange().setAfter(after).setLimit(limit);
    }
}
