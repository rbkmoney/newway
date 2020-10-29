package com.rbkmoney.newway.dao.invoicing.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.model.InvoicingKey;

import java.util.Collection;
import java.util.List;

public interface BatchDao<T> extends GenericDao {

    void saveBatch(List<T> records) throws DaoException;

    void updateBatch(List<T> records) throws DaoException;

    T get(InvoicingKey key) throws DaoException;

    void switchCurrent(Collection<InvoicingKey> switchIds) throws DaoException;
}
