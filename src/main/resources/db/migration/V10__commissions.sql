ALTER TABLE nw.payment ADD COLUMN fee BIGINT;
ALTER TABLE nw.payment ADD COLUMN provider_fee BIGINT;
ALTER TABLE nw.payment ADD COLUMN external_fee BIGINT;

ALTER TABLE nw.refund ADD COLUMN fee BIGINT;
ALTER TABLE nw.refund ADD COLUMN provider_fee BIGINT;
ALTER TABLE nw.refund ADD COLUMN external_fee BIGINT;

ALTER TABLE nw.adjustment ADD COLUMN fee BIGINT;
ALTER TABLE nw.adjustment ADD COLUMN provider_fee BIGINT;
ALTER TABLE nw.adjustment ADD COLUMN external_fee BIGINT;