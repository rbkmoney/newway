package com.rbkmoney.newway.dao.dominant.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.domain.tables.pojos.Inspector;
import com.rbkmoney.newway.domain.tables.records.InspectorRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.Tables.INSPECTOR;

@Component
public class InspectorDaoImpl extends AbstractGenericDao implements DomainObjectDao<Inspector, Integer> {

    public InspectorDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long save(Inspector inspector) throws DaoException {
        InspectorRecord inspectorRecord = getDslContext().newRecord(INSPECTOR, inspector);
        Query query = getDslContext().insertInto(INSPECTOR).set(inspectorRecord).returning(INSPECTOR.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public void updateNotCurrent(Integer inspectorId) throws DaoException {
        Query query = getDslContext().update(INSPECTOR).set(INSPECTOR.CURRENT, false)
                .where(INSPECTOR.INSPECTOR_REF_ID.eq(inspectorId).and(INSPECTOR.CURRENT));
        executeOne(query);
    }
}
