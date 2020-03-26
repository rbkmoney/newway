ALTER TABLE nw.party ADD COLUMN sequence_id BIGINT;
ALTER TABLE nw.party ADD COLUMN change_id INT;

ALTER TABLE nw.shop ADD COLUMN sequence_id BIGINT;
ALTER TABLE nw.shop ADD COLUMN change_id INT;

ALTER TABLE nw.contract ADD COLUMN sequence_id BIGINT;
ALTER TABLE nw.contract ADD COLUMN change_id INT;

ALTER TABLE nw.contractor ADD COLUMN sequence_id BIGINT;
ALTER TABLE nw.contractor ADD COLUMN change_id INT;