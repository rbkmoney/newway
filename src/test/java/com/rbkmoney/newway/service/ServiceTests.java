package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.AbstractAppDaoTests;
import com.rbkmoney.newway.domain.tables.pojos.Rate;
import com.rbkmoney.newway.utils.RateSinkEventTestUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ServiceTests extends AbstractAppDaoTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RateService rateService;

    @Test
    public void rateServiceTest() {
        String sourceId = "CBR";

        RateSinkEventTestUtils.Dto dto = RateSinkEventTestUtils.create(sourceId);

        rateService.handleEvents(dto.getSinkEvent(), dto.getEvent());

        List<Rate> rates = jdbcTemplate.query(
                "SELECT * FROM nw.rate AS rate WHERE rate.source_id = ? AND rate.current",
                new Object[]{sourceId},
                new BeanPropertyRowMapper(Rate.class)
        );
        assertEquals(4, rates.size());
    }
}
