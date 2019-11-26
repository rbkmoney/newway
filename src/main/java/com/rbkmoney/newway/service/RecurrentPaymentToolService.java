package com.rbkmoney.newway.service;

import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolEvent;
import com.rbkmoney.newway.dao.recurrent_payment_tool.iface.RecurrentPaymentToolDao;
import com.rbkmoney.newway.poller.event_stock.impl.recurrent_payment_tool.AbstractRecurrentPaymentToolHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecurrentPaymentToolService implements EventService<RecurrentPaymentToolEvent, RecurrentPaymentToolEvent>{

    private final RecurrentPaymentToolDao recurrentPaymentToolDao;

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
                change -> recurrentPaymentToolHandlers.forEach
                        (handler -> {
                            if (handler.accept(change)) {
                                handler.handle(change, event, cnt.getAndIncrement());
                            }
                        }));
    }
}
