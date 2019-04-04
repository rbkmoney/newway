package com.rbkmoney.newway.dao.destination.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.common.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.destination.iface.DestinationDao;
import com.rbkmoney.newway.domain.tables.pojos.Destination;
import com.rbkmoney.newway.domain.tables.records.DestinationRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.tables.Destination.DESTINATION;

@Component
public class DestinationDaoImpl extends AbstractGenericDao implements DestinationDao {

    private final RowMapper<Destination> destinationRowMapper;

    @Autowired
    public DestinationDaoImpl(DataSource dataSource) {
        super(dataSource);
        destinationRowMapper = new RecordRowMapper<>(DESTINATION, Destination.class);
    }

    @Override
    public Long getLastEventId() throws DaoException {
        Query query = getDslContext().select(DESTINATION.EVENT_ID.max()).from(DESTINATION);
        return fetchOne(query, Long.class);
    }

    @Override
    public Long save(Destination destination) throws DaoException {
        DestinationRecord record = getDslContext().newRecord(DESTINATION, destination);
        Query query = getDslContext().insertInto(DESTINATION).set(record).returning(DESTINATION.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOneWithReturn(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Destination get(String destinationId) throws DaoException {
        Query query = getDslContext().selectFrom(DESTINATION)
                .where(DESTINATION.DESTINATION_ID.eq(destinationId)
                        .and(DESTINATION.CURRENT));

        return fetchOne(query, destinationRowMapper);
    }

    @Override
    public void updateNotCurrent(String destinationId) throws DaoException {
        Query query = getDslContext().update(DESTINATION).set(DESTINATION.CURRENT, false)
                .where(
                        DESTINATION.DESTINATION_ID.eq(destinationId)
                                .and(DESTINATION.CURRENT)
                );
        execute(query);
    }
}
