package com.rbkmoney.newway.dao.identity.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Challenge;
import com.rbkmoney.newway.exception.DaoException;

import java.util.Optional;

public interface ChallengeDao extends GenericDao {

    Optional<Long> save(Challenge challenge) throws DaoException;

    Challenge get(String identityId, String challengeId) throws DaoException;

    void updateNotCurrent(String identityId, Long challengeId) throws DaoException;

}
