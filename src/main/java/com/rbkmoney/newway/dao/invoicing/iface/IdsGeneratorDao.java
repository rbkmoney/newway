package com.rbkmoney.newway.dao.invoicing.iface;

import com.rbkmoney.newway.exception.DaoException;

import java.util.List;

public interface IdsGeneratorDao {
    List<Long> get(int size) throws DaoException;
}
