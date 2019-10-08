package com.rbkmoney.newway.dao.source.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.source.iface.SourceDao;
import com.rbkmoney.newway.domain.tables.pojos.Source;
import com.rbkmoney.newway.domain.tables.records.SourceRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.tables.Source.SOURCE;

@Component
public class SourceDaoImpl extends AbstractGenericDao implements SourceDao {

    private final RowMapper<Source> sourceRowMapper;

    @Autowired
    public SourceDaoImpl(DataSource dataSource) {
        super(dataSource);
        sourceRowMapper = new RecordRowMapper<>(SOURCE, Source.class);
    }

    @Override
    public Long getLastEventId() throws DaoException {
        Query query = getDslContext().select(SOURCE.EVENT_ID.max()).from(SOURCE);
        return fetchOne(query, Long.class);
    }

    @Override
    public Long save(Source source) throws DaoException {
        SourceRecord record = getDslContext().newRecord(SOURCE, source);
        Query query = getDslContext().insertInto(SOURCE).set(record).returning(SOURCE.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Source get(String sourceId) throws DaoException {
        Query query = getDslContext().selectFrom(SOURCE)
                .where(SOURCE.SOURCE_ID.eq(sourceId)
                        .and(SOURCE.CURRENT));

        return fetchOne(query, sourceRowMapper);
    }

    @Override
    public void updateNotCurrent(String sourceId) throws DaoException {
        Query query = getDslContext().update(SOURCE).set(SOURCE.CURRENT, false)
                .where(
                        SOURCE.SOURCE_ID.eq(sourceId)
                                .and(SOURCE.CURRENT)
                );
        execute(query);
    }
}