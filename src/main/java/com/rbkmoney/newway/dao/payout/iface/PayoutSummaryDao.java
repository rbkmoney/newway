package com.rbkmoney.newway.dao.payout.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.PayoutSummary;
import com.rbkmoney.newway.exception.DaoException;

import java.util.List;

public interface PayoutSummaryDao extends GenericDao {

    void save(List<PayoutSummary> payoutSummaryList) throws DaoException;

    List<PayoutSummary> getByPytId(Long pytId) throws DaoException;
}
