package com.rbkmoney.newway.service;

import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.newway.dao.AbstractAppDaoTests;
import com.rbkmoney.newway.domain.tables.pojos.Rate;
import com.rbkmoney.newway.utils.RateSinkEventTestUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class RateServiceTests extends AbstractAppDaoTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RateService rateService;

    @Test
    public void rateServiceTest() {
        jdbcTemplate.execute("truncate table nw.rate cascade");
        String sourceId = "CBR";

        rateService.handleEvents(RateSinkEventTestUtils.create(sourceId));

        List<Rate> rates = jdbcTemplate.query(
                "SELECT * FROM nw.rate AS rate WHERE rate.source_id = ? AND rate.current",
                new Object[] {sourceId},
                new BeanPropertyRowMapper(Rate.class)
        );
        assertEquals(4, rates.size());
    }

    @Test
    public void rateServiceDuplicationTest() {
        jdbcTemplate.execute("truncate table nw.rate cascade");
        String sourceId = "CBR";

        List<SinkEvent> sinkEvents = RateSinkEventTestUtils.create(sourceId);
        rateService.handleEvents(sinkEvents);
        rateService.handleEvents(sinkEvents);

        List<Rate> rates = jdbcTemplate.query(
                "SELECT * FROM nw.rate AS rate WHERE rate.source_id = ? AND rate.current",
                new Object[] {sourceId},
                new BeanPropertyRowMapper(Rate.class)
        );
        assertEquals(4, rates.size());
    }

    @Test
    public void rateServiceDuplicationWhenPaymentSystemIsNullTest() {
        jdbcTemplate.execute("truncate table nw.rate cascade");
        String sourceId = "CBR";

        List<SinkEvent> sinkEvents = RateSinkEventTestUtils.create(sourceId, "payment_system");
        rateService.handleEvents(sinkEvents);
        rateService.handleEvents(sinkEvents);

        List<Rate> rates = jdbcTemplate.query(
                "SELECT * FROM nw.rate AS rate WHERE rate.source_id = ? AND rate.current",
                new Object[] {sourceId},
                new BeanPropertyRowMapper(Rate.class)
        );
        assertEquals(4, rates.size());
    }

}
