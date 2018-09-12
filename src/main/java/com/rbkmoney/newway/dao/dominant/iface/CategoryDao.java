package com.rbkmoney.newway.dao.dominant.iface;

import com.rbkmoney.newway.dao.common.iface.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Category;
import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.exception.DaoException;

public interface CategoryDao extends GenericDao {

    Long save(Category category) throws DaoException;

    void updateNotCurrent(Integer categoryId) throws DaoException;
}
