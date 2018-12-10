package com.rbkmoney.newway.poller.event_stock.impl.rate;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.rate.iface.RateDao;
import com.rbkmoney.newway.domain.tables.pojos.Rate;
import com.rbkmoney.xrates.base.Rational;
import com.rbkmoney.xrates.base.TimestampInterval;
import com.rbkmoney.xrates.rate.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RateCreatedHandler extends AbstractRateHandler {

    private final RateDao rateDao;

    private final Filter filter;

    public RateCreatedHandler(RateDao rateDao) {
        this.rateDao = rateDao;
        this.filter = new PathConditionFilter(
                new PathConditionRule("created", new IsNullCondition().not())
        );
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        log.info("Start rate created handling, eventId={}, walletId={}", event.getId(), event.getSource());
        Rate rate = new Rate();

        // SinkEvent
        rate.setEventId(event.getId());
        rate.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        rate.setEventSourceId(event.getSource());

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

                    rateDao.save(rate);
                }
        );

        log.info("Rate have been saved, eventId={}, walletId={}", event.getId(), event.getSource());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }
}
