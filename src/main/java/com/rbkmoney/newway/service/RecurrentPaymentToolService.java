package com.rbkmoney.newway.service;

import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolEvent;
import com.rbkmoney.newway.dao.recurrent_payment_tool.iface.RecurrentPaymentToolDao;
import com.rbkmoney.newway.poller.event_stock.impl.recurrent_payment_tool.AbstractRecurrentPaymentToolHandler;
import com.rbkmoney.newway.poller.event_stock.impl.source.AbstractSourceHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecurrentPaymentToolService implements EventService<RecurrentPaymentToolEvent, RecurrentPaymentToolEvent>{

    private RecurrentPaymentToolDao recurrentPaymentToolDao;

    private final List<AbstractRecurrentPaymentToolHandler> recurrentPaymentToolHandlers;

    public Optional<Long> getLastEventId() {
        Optional<Long> lastEventId = Optional.ofNullable(recurrentPaymentToolDao.getLastEventId());
        log.info("Last recurrent payment tool eventId={}", lastEventId);
        return lastEventId;
    }

    @Override
    public void handleEvents(RecurrentPaymentToolEvent event, RecurrentPaymentToolEvent payload) {
        AtomicInteger cnt = new AtomicInteger(0);
        event.getPayload().forEach(
                cc -> recurrentPaymentToolHandlers.forEach
                        (ph -> {
                            if (ph.accept(cc)) {
                                ph.handle(cc, event, cnt.getAndIncrement());
                            }
                        }));
    }
}
