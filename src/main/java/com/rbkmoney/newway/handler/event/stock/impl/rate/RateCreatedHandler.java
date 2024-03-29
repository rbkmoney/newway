package com.rbkmoney.newway.handler.event.stock.impl.rate;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.rate.iface.RateDao;
import com.rbkmoney.newway.domain.tables.pojos.Rate;
import com.rbkmoney.xrates.base.Rational;
import com.rbkmoney.xrates.base.TimestampInterval;
import com.rbkmoney.xrates.rate.Change;
import com.rbkmoney.xrates.rate.Currency;
import com.rbkmoney.xrates.rate.ExchangeRateCreated;
import com.rbkmoney.xrates.rate.ExchangeRateData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateCreatedHandler implements RateHandler {

    private final RateDao rateDao;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("created", new IsNullCondition().not()));

    @Override
    public void handle(Change change, MachineEvent event, Integer changeId) {
        if (change.getCreated().getExchangeRateData().getQuotes().isEmpty()) {
            log.warn("Quotes is empty, SinkEvent will not be saved, eventId={}, sourceId={}",
                    event.getEventId(), event.getSourceId());
            return;
        }
        log.info("Start rate created handling, eventId={}, sourceId={}", event.getEventId(), event.getSourceId());
        Rate rate = new Rate();

        // SinkEvent
        rate.setSourceId(event.getSourceId());
        rate.setSequenceId(event.getEventId());
        rate.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));

        // Event
        // <-- empty now

        // Change
        ExchangeRateCreated exchangeRateCreated = change.getCreated();

        // ExchangeRateCreated
        ExchangeRateData exchangeRateData = exchangeRateCreated.getExchangeRateData();

        // ExchangeRateData
        TimestampInterval interval = exchangeRateData.getInterval();
        rate.setLowerBoundInclusive(TypeUtil.stringToLocalDateTime(interval.getLowerBoundInclusive()));
        rate.setUpperBoundExclusive(TypeUtil.stringToLocalDateTime(interval.getUpperBoundExclusive()));

        List<Long> ids = rateDao.getIds(event.getSourceId());
        AtomicBoolean shouldUpdate = new AtomicBoolean(false);
        exchangeRateData.getQuotes().forEach(
                quote -> {
                    // Quote
                    Currency source = quote.getSource();
                    Currency destination = quote.getDestination();
                    Rational exchangeRate = quote.getExchangeRate();

                    // Currency
                    rate.setSourceSymbolicCode(source.getSymbolicCode());
                    rate.setSourceExponent(source.getExponent());
                    rate.setDestinationSymbolicCode(destination.getSymbolicCode());
                    rate.setDestinationExponent(destination.getExponent());

                    // ExchangeRate
                    rate.setExchangeRateRationalP(exchangeRate.getP());
                    rate.setExchangeRateRationalQ(exchangeRate.getQ());

                    Long id = rateDao.save(rate);
                    shouldUpdate.set(id != null);
                }
        );
        if (shouldUpdate.get()) {
            rateDao.updateNotCurrent(ids);
        }
        log.info("Rate have been saved, eventId={}, sourceId={}", event.getEventId(), event.getSourceId());
    }

}
