package com.rbkmoney.newway.dao.party.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.party.iface.RevisionDao;
import com.rbkmoney.newway.exception.DaoException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class RevisionDaoImpl extends AbstractGenericDao implements RevisionDao {

    public RevisionDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void saveShopsRevision(String partyId, long revision) throws DaoException {
        getNamedParameterJdbcTemplate().update("insert into nw.shop_revision(obj_id, revision) " +
                        "select id, :revision from nw.shop where party_id = :partyId and current",
                new MapSqlParameterSource()
                        .addValue("party_id", partyId)
                        .addValue("revision", revision));
    }

    @Override
    public void saveContractsRevision(String partyId, long revision) throws DaoException {
        getNamedParameterJdbcTemplate().update("insert into nw.contract_revision(obj_id, revision) " +
                        "select id, :revision from nw.contract where party_id = :party_id and current",
                new MapSqlParameterSource()
                        .addValue("party_id", partyId)
                        .addValue("revision", revision));
    }

    @Override
    public void saveContractorsRevision(String partyId, long revision) throws DaoException {
        getNamedParameterJdbcTemplate().update("insert into nw.contractor_revision(obj_id, revision) " +
                        "select id, :revision from nw.contractor where party_id = :party_id and current",
                new MapSqlParameterSource()
                        .addValue("party_id", partyId)
                        .addValue("revision", revision));
    }
}
