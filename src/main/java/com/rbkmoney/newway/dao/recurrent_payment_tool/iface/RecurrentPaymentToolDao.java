package com.rbkmoney.newway.dao.recurrent_payment_tool.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.RecurrentPaymentTool;
import com.rbkmoney.newway.exception.DaoException;


public interface RecurrentPaymentToolDao extends GenericDao {

    Long getLastEventId() throws DaoException;

    Long save(RecurrentPaymentTool source) throws DaoException;

    RecurrentPaymentTool get(String recurrentPaymentToolId) throws DaoException;

    void updateNotCurrent(Long rptId) throws DaoException;
}
