ALTER TABLE nw.deposit_adjustment
    ALTER COLUMN amount DROP NOT NULL;
ALTER TABLE nw.deposit_adjustment
    ALTER COLUMN currency_code DROP NOT NULL;
