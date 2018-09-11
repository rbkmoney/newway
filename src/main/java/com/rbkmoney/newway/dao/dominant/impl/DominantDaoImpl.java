package com.rbkmoney.newway.dao.dominant.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.dominant.iface.DominantDao;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.Tables.CATEGORY;

@Component
public class DominantDaoImpl extends AbstractGenericDao implements DominantDao {

    public DominantDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long getLastVersionId() throws DaoException {
        Query query = getDslContext().select(DSL.max(DSL.field("version_id"))).from(
                getDslContext().select(CATEGORY.VERSION_ID.max().as("version_id")).from(CATEGORY));
        return fetchOne(query, Long.class);
    }
}
