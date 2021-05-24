package com.rbkmoney.newway.util;

import com.rbkmoney.damsel.domain.Contractor;
import com.rbkmoney.newway.TestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ContractorUtilTest {

    public static final String CREATED_AT = "2021-05-18T16:46:22.405695Z";
    public static final long SEQ_ID = 1L;
    public static final Integer CHANGE_ID = 1;
    public static final Integer CLAIM_EFFECT_ID = 2;

    @Test
    void convertContractorWithoutCountryCode() {
        Contractor contractor = TestData.buildContractor();
        contractor.getLegalEntity().getInternationalLegalEntity().setCountry(null);
        long seqId = 1L;
        String partyId = TestData.randomString();
        String contractorId = TestData.randomString();
        Integer changeId = 1;
        Integer claimEffectId = 2;

        com.rbkmoney.newway.domain.tables.pojos.Contractor actualContractor = ContractorUtil
                .convertContractor(seqId, CREATED_AT, partyId, contractor, contractorId, changeId, claimEffectId);

        assertNull(actualContractor.getInternationalLegalEntityCountryCode());

    }

    @Test
    void convertContractorWithCountryCode() {
        Contractor contractor = TestData.buildContractor();
        String partyId = TestData.randomString();
        String contractorId = TestData.randomString();

        com.rbkmoney.newway.domain.tables.pojos.Contractor actualContractor = ContractorUtil
                .convertContractor(SEQ_ID, CREATED_AT, partyId, contractor, contractorId, CHANGE_ID, CLAIM_EFFECT_ID);

        assertEquals(SEQ_ID, (long) actualContractor.getSequenceId());
        assertEquals(contractorId, actualContractor.getContractorId());
        assertEquals(CHANGE_ID, actualContractor.getChangeId());
        assertEquals(CLAIM_EFFECT_ID, actualContractor.getClaimEffectId());
        assertEquals(contractor.getLegalEntity().getInternationalLegalEntity().getCountry().getId().name(),
                actualContractor.getInternationalLegalEntityCountryCode());
    }
}