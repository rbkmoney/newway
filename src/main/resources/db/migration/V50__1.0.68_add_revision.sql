ALTER TABLE nw.shop DROP CONSTRAINT shop_uniq
, ADD CONSTRAINT shop_uniq UNIQUE(party_id, sequence_id, change_id, claim_effect_id, revision);

ALTER TABLE nw.contract DROP CONSTRAINT contract_uniq
, ADD CONSTRAINT contract_uniq UNIQUE(party_id, sequence_id, change_id, claim_effect_id, revision);

ALTER TABLE nw.contractor DROP CONSTRAINT contractor_uniq
, ADD CONSTRAINT contractor_uniq UNIQUE(party_id, sequence_id, change_id, claim_effect_id, revision);