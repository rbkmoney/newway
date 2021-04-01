package com.rbkmoney.newway.dao;

import com.rbkmoney.newway.dao.source.iface.SourceDao;
import com.rbkmoney.newway.domain.tables.pojos.Source;
import com.rbkmoney.newway.exception.NotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

public class SourceDaoTest extends AbstractAppDaoTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SourceDao sourceDao;

    @Test(expected = NotFoundException.class)
    public void sourceDaoTest() {
        jdbcTemplate.execute("truncate table nw.source cascade");
        Source source = random(Source.class);
        source.setCurrent(true);
        Long id = sourceDao.save(source).get();
        source.setId(id);
        Source actual = sourceDao.get(source.getSourceId());
        assertEquals(source, actual);
        sourceDao.updateNotCurrent(actual.getId());

        //check duplicate not error
        sourceDao.save(source);

        sourceDao.get(source.getSourceId());
    }

}
