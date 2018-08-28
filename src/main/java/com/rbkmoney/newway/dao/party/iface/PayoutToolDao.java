package com.rbkmoney.newway.dao.party.iface;

import com.rbkmoney.newway.dao.common.iface.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.PayoutTool;
import com.rbkmoney.newway.exception.DaoException;

import java.util.List;

public interface PayoutToolDao extends GenericDao {
    void save(List<PayoutTool> payoutToolList) throws DaoException;
    List<PayoutTool> getByCntrctId(Long cntrctId) throws DaoException;
}
