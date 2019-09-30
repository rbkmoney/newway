package com.rbkmoney.newway.dao.dominant.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.domain.tables.pojos.Category;
import com.rbkmoney.newway.domain.tables.records.CategoryRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.Tables.CATEGORY;

@Component
public class CategoryDaoImpl extends AbstractGenericDao implements DomainObjectDao<Category, Integer> {

    public CategoryDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long save(Category category) throws DaoException {
        CategoryRecord categoryRecord = getDslContext().newRecord(CATEGORY, category);
        Query query = getDslContext().insertInto(CATEGORY).set(categoryRecord).returning(CATEGORY.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public void updateNotCurrent(Integer categoryId) throws DaoException {
        Query query = getDslContext().update(CATEGORY).set(CATEGORY.CURRENT, false)
                .where(CATEGORY.CATEGORY_REF_ID.eq(categoryId).and(CATEGORY.CURRENT));
        executeOne(query);
    }
}
