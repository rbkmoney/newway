package com.rbkmoney.newway.dao.recurrent.payment.tool.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.RecurrentPaymentTool;
import com.rbkmoney.newway.exception.DaoException;

import java.util.Optional;


public interface RecurrentPaymentToolDao extends GenericDao {

    Optional<Long> save(RecurrentPaymentTool source) throws DaoException;

    RecurrentPaymentTool get(String recurrentPaymentToolId) throws DaoException;

    void updateNotCurrent(Long rptId) throws DaoException;

}
