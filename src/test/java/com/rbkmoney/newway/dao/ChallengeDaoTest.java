package com.rbkmoney.newway.dao;

import com.rbkmoney.newway.dao.identity.iface.ChallengeDao;
import com.rbkmoney.newway.domain.tables.pojos.Challenge;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class ChallengeDaoTest extends AbstractAppDaoTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ChallengeDao challengeDao;

    @Test
    public void challengeDaoTest() {
        jdbcTemplate.execute("truncate table nw.challenge cascade");
        Challenge challenge = random(Challenge.class);
        challenge.setCurrent(true);
        Long id = challengeDao.save(challenge).get();
        challenge.setId(id);
        Challenge actual = challengeDao.get(challenge.getIdentityId(), challenge.getChallengeId());
        assertEquals(challenge, actual);
        challengeDao.updateNotCurrent(challenge.getIdentityId(), actual.getId());
        assertNull(challengeDao.get(challenge.getIdentityId(), challenge.getChallengeId()));

        //check duplicate not error
        challengeDao.save(challenge);
    }

}
