package com.rbkmoney.newway.poller.dominant.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.dominant.impl.CalendarDaoImpl;
import com.rbkmoney.newway.domain.tables.pojos.Calendar;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

public class CalendarHandlerTest extends AbstractIntegrationTest {

    @Autowired
    private CalendarDaoImpl calendarDao;

    @Test
    public void test() {
        Calendar category = random(Calendar.class);
        category.setCurrent(true);
        calendarDao.save(category);
        calendarDao.updateNotCurrent(category.getCalendarRefId());
    }
}
