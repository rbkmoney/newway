package com.rbkmoney.newway.dao.identity.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.identity.iface.ChallengeDao;
import com.rbkmoney.newway.domain.tables.pojos.Challenge;
import com.rbkmoney.newway.domain.tables.records.ChallengeRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;

import static com.rbkmoney.newway.domain.tables.Challenge.CHALLENGE;

@Component
public class ChallengeDaoImpl extends AbstractGenericDao implements ChallengeDao {

    private final RowMapper<Challenge> challengeRowMapper;

    @Autowired
    public ChallengeDaoImpl(DataSource dataSource) {
        super(dataSource);
        challengeRowMapper = new RecordRowMapper<>(CHALLENGE, Challenge.class);
    }

    @Override
    public Optional<Long> save(Challenge challenge) throws DaoException {
        ChallengeRecord record = getDslContext().newRecord(CHALLENGE, challenge);
        Query query = getDslContext()
                .insertInto(CHALLENGE)
                .set(record)
                .onConflict(CHALLENGE.IDENTITY_ID, CHALLENGE.CHALLENGE_ID, CHALLENGE.SEQUENCE_ID)
                .doNothing()
                .returning(CHALLENGE.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public Challenge get(String identityId, String challengeId) throws DaoException {
        Query query = getDslContext().selectFrom(CHALLENGE)
                .where(
                        CHALLENGE.IDENTITY_ID.eq(identityId)
                        .and(CHALLENGE.CHALLENGE_ID.eq(challengeId))
                        .and(CHALLENGE.CURRENT)
                );

        return fetchOne(query, challengeRowMapper);
    }

    @Override
    public void updateNotCurrent(String identityId, Long challengeId) throws DaoException {
        Query query = getDslContext().update(CHALLENGE).set(CHALLENGE.CURRENT, false)
                .where(
                        CHALLENGE.ID.eq(challengeId)
                                .and(CHALLENGE.IDENTITY_ID.eq(identityId))
                                .and(CHALLENGE.CURRENT)
                );
        execute(query);
    }
}
