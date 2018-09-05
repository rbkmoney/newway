TRUNCATE nw.party, nw.contract, nw.contractor, nw.shop CASCADE;

ALTER TABLE nw.contract ADD COLUMN revision BIGINT NOT NULL;
ALTER TABLE nw.contractor ADD COLUMN revision BIGINT NOT NULL;
ALTER TABLE nw.shop ADD COLUMN revision BIGINT NOT NULL;