ALTER TABLE nw.adjustment
    ADD amount BIGINT;

UPDATE nw.adjustment a
SET amount = (SELECT fee
              FROM nw.payment p
              WHERE p.payment_id = a.payment_id AND p.invoice_id = a.invoice_id AND p.current) - a.fee;

ALTER TABLE nw.adjustment ALTER COLUMN amount SET NOT NULL;

ALTER TABLE nw.adjustment
    DROP COLUMN fee,
    DROP COLUMN external_fee,
    DROP COLUMN provider_fee;
