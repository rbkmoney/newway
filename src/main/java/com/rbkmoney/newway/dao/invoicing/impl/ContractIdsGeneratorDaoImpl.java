package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.newway.dao.invoicing.iface.IdsGeneratorDao;
import com.rbkmoney.newway.exception.DaoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedRuntimeException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractIdsGeneratorDaoImpl implements IdsGeneratorDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Long> get(int size) throws DaoException {
        try {
            String sql = "select nextval('nw.cntrct_seq') from generate_series(1, :size)";
            MapSqlParameterSource parameterSource = new MapSqlParameterSource().addValue("size", size);
            return jdbcTemplate.queryForList(sql, parameterSource, Long.class);
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
    }
}
