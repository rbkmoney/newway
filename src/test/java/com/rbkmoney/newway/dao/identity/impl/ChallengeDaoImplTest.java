package com.rbkmoney.newway.dao.identity.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.identity.iface.ChallengeDao;
import com.rbkmoney.newway.domain.tables.pojos.Challenge;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class ChallengeDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private ChallengeDao challengeDao;

    @Test
    public void test() {
        Challenge challenge = random(Challenge.class);
        challenge.setCurrent(true);
        Long id = challengeDao.save(challenge);
        challenge.setId(id);
        assertEquals(challenge, challengeDao.get(challenge.getIdentityId(), challenge.getChallengeId()));
        challengeDao.updateNotCurrent(challenge.getIdentityId(), challenge.getChallengeId());
        assertNull(challengeDao.get(challenge.getIdentityId(), challenge.getChallengeId()));
    }

}
