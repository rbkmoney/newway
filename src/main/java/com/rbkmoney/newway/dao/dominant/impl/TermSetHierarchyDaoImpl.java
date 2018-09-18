package com.rbkmoney.newway.dao.dominant.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.domain.tables.pojos.TermSetHierarchy;
import com.rbkmoney.newway.domain.tables.records.TermSetHierarchyRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.Tables.TERM_SET_HIERARCHY;

@Component
public class TermSetHierarchyDaoImpl extends AbstractGenericDao implements DomainObjectDao<TermSetHierarchy, Integer> {

    public TermSetHierarchyDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long save(TermSetHierarchy termSetHierarchy) throws DaoException {
        TermSetHierarchyRecord termSetHierarchyRecord = getDslContext().newRecord(TERM_SET_HIERARCHY, termSetHierarchy);
        Query query = getDslContext().insertInto(TERM_SET_HIERARCHY).set(termSetHierarchyRecord).returning(TERM_SET_HIERARCHY.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOneWithReturn(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public void updateNotCurrent(Integer termSetHierarchyId) throws DaoException {
        Query query = getDslContext().update(TERM_SET_HIERARCHY).set(TERM_SET_HIERARCHY.CURRENT, false)
                .where(TERM_SET_HIERARCHY.TERM_SET_HIERARCHY_REF_ID.eq(termSetHierarchyId).and(TERM_SET_HIERARCHY.CURRENT));
        executeOne(query);
    }
}
