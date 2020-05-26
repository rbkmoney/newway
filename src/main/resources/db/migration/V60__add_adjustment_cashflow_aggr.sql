ALTER TABLE nw.adjustment
    ADD amount BIGINT default 0;

UPDATE nw.adjustment a
SET amount = (SELECT fee
              FROM nw.payment p
              WHERE p.payment_id = a.payment_id) - a.fee;

ALTER TABLE nw.adjustment
    DROP COLUMN fee,
    DROP COLUMN external_fee,
    DROP COLUMN provider_fee;
