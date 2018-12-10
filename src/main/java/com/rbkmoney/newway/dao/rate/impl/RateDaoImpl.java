package com.rbkmoney.newway.dao.rate.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.common.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.rate.iface.RateDao;
import com.rbkmoney.newway.domain.tables.pojos.Rate;
import com.rbkmoney.newway.domain.tables.records.RateRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.tables.Rate.RATE;

@Component
public class RateDaoImpl extends AbstractGenericDao implements RateDao {

    private final RowMapper<Rate> rateRowMapper;

    @Autowired
    public RateDaoImpl(@Qualifier("dataSource") DataSource dataSource) {
        super(dataSource);
        rateRowMapper = new RecordRowMapper<>(RATE, Rate.class);
    }

    @Override
    public Long getLastEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(RATE.EVENT_ID)).from(RATE);
        return fetchOne(query, Long.class);
    }

    @Override
    public Long save(Rate rate) throws DaoException {
        RateRecord record = getDslContext().newRecord(RATE, rate);
        Query query = getDslContext().insertInto(RATE).set(record).returning(RATE.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOneWithReturn(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Rate get(String eventSourceId) throws DaoException {
        Query query = getDslContext().selectFrom(RATE)
                .where(RATE.EVENT_SOURCE_ID.eq(eventSourceId).and(RATE.CURRENT));
        return fetchOne(query, rateRowMapper);
    }

    @Override
    public void updateNotCurrent(String eventSourceId) throws DaoException {
        Query query = getDslContext().update(RATE).set(RATE.CURRENT, false)
                .where(RATE.EVENT_SOURCE_ID.eq(eventSourceId).and(RATE.CURRENT));
        executeOne(query);
    }
}