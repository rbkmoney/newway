ALTER TABLE nw.deposit_adjustment
    ALTER COLUMN amount DROP NOT NULL;

ALTER TABLE nw.deposit_adjustment
    ALTER COLUMN currency_code DROP NOT NULL;

ALTER TABLE nw.deposit_adjustment
    ADD COLUMN party_revision BIGINT NOT NULL DEFAULT 0;

ALTER TABLE nw.deposit_adjustment
    ADD COLUMN domain_revision BIGINT NOT NULL DEFAULT 0;

ALTER TABLE nw.deposit_revert
    ADD COLUMN party_revision BIGINT NOT NULL DEFAULT 0;

ALTER TABLE nw.deposit_revert
    ADD COLUMN domain_revision BIGINT NOT NULL DEFAULT 0;
