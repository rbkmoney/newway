package com.rbkmoney.newway.dao.recurrent_payment_tool.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.recurrent_payment_tool.iface.RecurrentPaymentToolDao;
import com.rbkmoney.newway.domain.tables.pojos.RecurrentPaymentTool;
import com.rbkmoney.newway.domain.tables.records.RecurrentPaymentToolRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import static com.rbkmoney.newway.domain.tables.RecurrentPaymentTool.RECURRENT_PAYMENT_TOOL;

import javax.sql.DataSource;
import java.util.Optional;

@Component
public class RecurrentPaymentToolDaoImpl extends AbstractGenericDao implements RecurrentPaymentToolDao {

    private final RowMapper<RecurrentPaymentTool> recurrentPaymentToolRowMapper;

    public RecurrentPaymentToolDaoImpl(DataSource dataSource) {
        super(dataSource);
        recurrentPaymentToolRowMapper = new RecordRowMapper<>(RECURRENT_PAYMENT_TOOL, RecurrentPaymentTool.class);
    }

    @Override
    public Long getLastEventId() throws DaoException {
        Query query = getDslContext().select(RECURRENT_PAYMENT_TOOL.EVENT_ID.max()).from(RECURRENT_PAYMENT_TOOL);
        return fetchOne(query, Long.class);
    }

    @Override
    public Long save(RecurrentPaymentTool source) throws DaoException {
        RecurrentPaymentToolRecord record = getDslContext().newRecord(RECURRENT_PAYMENT_TOOL, source);
        Query query = getDslContext().insertInto(RECURRENT_PAYMENT_TOOL)
                .set(record)
                .onConflict(RECURRENT_PAYMENT_TOOL.RECURRENT_PAYMENT_TOOL_ID,
                        RECURRENT_PAYMENT_TOOL.SEQUENCE_ID,
                        RECURRENT_PAYMENT_TOOL.CHANGE_ID)
                .doNothing()
                .returning(RECURRENT_PAYMENT_TOOL.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue).orElse(null);
    }

    @Override
    public RecurrentPaymentTool get(String recurrentPaymentToolId) throws DaoException {
        Query query = getDslContext().selectFrom(RECURRENT_PAYMENT_TOOL)
                .where(RECURRENT_PAYMENT_TOOL.RECURRENT_PAYMENT_TOOL_ID.eq(recurrentPaymentToolId)
                        .and(RECURRENT_PAYMENT_TOOL.CURRENT));

        return fetchOne(query, recurrentPaymentToolRowMapper);
    }

    @Override
    public void updateNotCurrent(Long id) throws DaoException {
        Query query = getDslContext().update(RECURRENT_PAYMENT_TOOL).set(RECURRENT_PAYMENT_TOOL.CURRENT, false)
                .where(RECURRENT_PAYMENT_TOOL.ID.eq(id));
        executeOne(query);
    }
}

