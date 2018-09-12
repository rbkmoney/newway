package com.rbkmoney.newway.dao.dominant.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.domain.tables.pojos.Calendar;
import com.rbkmoney.newway.domain.tables.records.CalendarRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.Tables.CALENDAR;

@Component
public class CalendarDaoImpl extends AbstractGenericDao implements DomainObjectDao<Calendar, Integer> {

    public CalendarDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long save(Calendar calendar) throws DaoException {
        CalendarRecord calendarRecord = getDslContext().newRecord(CALENDAR, calendar);
        Query query = getDslContext().insertInto(CALENDAR).set(calendarRecord).returning(CALENDAR.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOneWithReturn(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public void updateNotCurrent(Integer calendarId) throws DaoException {
        Query query = getDslContext().update(CALENDAR).set(CALENDAR.CURRENT, false)
                .where(CALENDAR.CALENDAR_REF_ID.eq(calendarId).and(CALENDAR.CURRENT));
        executeOne(query);
    }
}
