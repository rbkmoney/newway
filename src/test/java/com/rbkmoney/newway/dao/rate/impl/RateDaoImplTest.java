package com.rbkmoney.newway.dao.rate.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.rate.iface.RateDao;
import com.rbkmoney.newway.domain.tables.pojos.Rate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.*;

public class RateDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private RateDao rateDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void test() {
        Rate rate = random(Rate.class);
        rate.setCurrent(true);

        Long id = rateDao.save(rate);
        rate.setId(id);
        assertEquals(
                rate,
                jdbcTemplate.queryForObject(
                        "SELECT * FROM nw.rate WHERE id = ? ",
                        new Object[]{id},
                        new BeanPropertyRowMapper(Rate.class)
                )
        );

        rateDao.updateNotCurrent(rate.getSourceId());
        try {
            jdbcTemplate.queryForObject(
                    "SELECT * FROM nw.rate AS rate WHERE rate.id = ? AND rate.current",
                    new Object[]{id},
                    new BeanPropertyRowMapper(Rate.class)
            );
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof EmptyResultDataAccessException);
        }
    }
}
