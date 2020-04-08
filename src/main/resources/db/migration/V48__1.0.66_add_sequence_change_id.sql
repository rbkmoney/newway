ALTER TABLE nw.party ADD COLUMN sequence_id INT;
ALTER TABLE nw.party ADD COLUMN change_id INT;
ALTER TABLE nw.party ADD CONSTRAINT party_uniq UNIQUE (party_id, sequence_id, change_id);

ALTER TABLE nw.shop ADD COLUMN sequence_id INT;
ALTER TABLE nw.shop ADD COLUMN change_id INT;
ALTER TABLE nw.shop ADD COLUMN claim_effect_id INT;
ALTER TABLE nw.shop ADD CONSTRAINT shop_uniq UNIQUE (party_id, sequence_id, change_id, claim_effect_id);

ALTER TABLE nw.contract ADD COLUMN sequence_id INT;
ALTER TABLE nw.contract ADD COLUMN change_id INT;
ALTER TABLE nw.contract ADD COLUMN claim_effect_id INT;
ALTER TABLE nw.contract ADD CONSTRAINT contract_uniq UNIQUE (party_id, sequence_id, change_id, claim_effect_id);

ALTER TABLE nw.contractor ADD COLUMN sequence_id INT;
ALTER TABLE nw.contractor ADD COLUMN change_id INT;
ALTER TABLE nw.contractor ADD COLUMN claim_effect_id INT;
ALTER TABLE nw.contractor ADD CONSTRAINT contractor_uniq UNIQUE (party_id, sequence_id, change_id, claim_effect_id);