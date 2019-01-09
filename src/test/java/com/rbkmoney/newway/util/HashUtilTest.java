package com.rbkmoney.newway.util;

import com.rbkmoney.newway.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.Assert.*;

public class HashUtilTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testGetIntHash() {
        Integer javaHash = HashUtil.getIntHash("kek");
        Integer posgresHash = jdbcTemplate.queryForObject("select ('x0'||substr(md5('kek'), 1, 7))::bit(32)::int", Integer.class);
        assertEquals(javaHash, posgresHash);
    }
}
