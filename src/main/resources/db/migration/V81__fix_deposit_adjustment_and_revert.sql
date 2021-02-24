ALTER TABLE nw.deposit_adjustment
    ALTER COLUMN amount DROP NOT NULL;

ALTER TABLE nw.deposit_adjustment
    ALTER COLUMN currency_code DROP NOT NULL;

ALTER TABLE nw.deposit_adjustment
    ADD COLUMN party_revision BIGINT;
ALTER TABLE nw.deposit_adjustment
    ALTER COLUMN party_revision SET NOT NULL;

ALTER TABLE nw.deposit_adjustment
    ADD COLUMN domain_revision BIGINT;
ALTER TABLE nw.deposit_adjustment
    ALTER COLUMN domain_revision SET NOT NULL;

ALTER TABLE nw.deposit_revert
    ADD COLUMN party_revision BIGINT;
ALTER TABLE nw.deposit_revert
    ALTER COLUMN party_revision SET NOT NULL;

ALTER TABLE nw.deposit_revert
    ADD COLUMN domain_revision BIGINT;
ALTER TABLE nw.deposit_revert
    ALTER COLUMN domain_revision SET NOT NULL;
