package com.rbkmoney.newway.service;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.domain.tables.pojos.Rate;
import com.rbkmoney.xrates.base.TimestampInterval;
import com.rbkmoney.xrates.rate.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.Assert.assertEquals;

public class RateServiceTest extends AbstractIntegrationTest {

    @Autowired
    private RateService rateService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void test() {
        List<Quote> quotes = new ArrayList<Quote>() {{
            addAll(randomListOf(4, Quote.class));
        }};

        Event event = new Event(
                Collections.singletonList(
                        Change.created(
                                new ExchangeRateCreated(
                                        new ExchangeRateData(
                                                new TimestampInterval(
                                                        TypeUtil.temporalToString(random(LocalDateTime.class)),
                                                        TypeUtil.temporalToString(random(LocalDateTime.class))
                                                ),
                                                quotes
                                        )
                                )
                        )
                )
        );

        String sourceId = "CBR";

        SinkEvent sinkEvent = new SinkEvent(
                random(Long.class),
                TypeUtil.temporalToString(random(LocalDateTime.class)),
                sourceId,
                event
        );

        rateService.handleEvents(sinkEvent, event);

        List<Rate> rates = jdbcTemplate.query(
                "SELECT * FROM nw.rate AS rate WHERE rate.source_id = ? AND rate.current",
                new Object[]{sourceId},
                new BeanPropertyRowMapper(Rate.class)
        );
        assertEquals(4, rates.size());
    }
}