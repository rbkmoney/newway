package com.rbkmoney.newway.dao.rate.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.rate.iface.RateDao;
import com.rbkmoney.newway.domain.tables.pojos.Rate;
import com.rbkmoney.newway.domain.tables.records.RateRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.List;
import java.util.Optional;

import static com.rbkmoney.newway.domain.tables.Rate.RATE;

@Component
public class RateDaoImpl extends AbstractGenericDao implements RateDao {

    @Autowired
    public RateDaoImpl(@Qualifier("dataSource") DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long save(Rate rate) throws DaoException {
        RateRecord record = getDslContext().newRecord(RATE, rate);
        Query query = getDslContext().insertInto(RATE).set(record)
                .onConflict(RATE.SOURCE_ID, RATE.SEQUENCE_ID, RATE.CHANGE_ID, RATE.SOURCE_SYMBOLIC_CODE, RATE.DESTINATION_SYMBOLIC_CODE)
                .doNothing()
                .returning(RATE.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue).orElse(null);
    }

    @Override
    public List<Long> getIds(String sourceId) throws DaoException {
        return this.getNamedParameterJdbcTemplate()
                .queryForList("select id from nw.rate where source_id=:source_id and current",
                        new MapSqlParameterSource("source_id", sourceId), Long.class);
    }

    @Override
    public void updateNotCurrent(List<Long> ids) throws DaoException {
        if (ids != null && !ids.isEmpty()) {
            this.getNamedParameterJdbcTemplate()
                    .update("update nw.rate set current=false where id in (:ids)",
                            new MapSqlParameterSource("ids", ids));
        }
    }
}
