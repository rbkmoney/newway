CREATE TYPE nw.recurrent_token_source AS ENUM ('payment');

ALTER TABLE nw.payment ADD COLUMN is_recurring BOOL;
ALTER TABLE nw.payment ADD COLUMN recurrent_intention_token_source nw.recurrent_token_source;
ALTER TABLE nw.payment ADD COLUMN recurrent_intention_token_source_invoice_id CHARACTER VARYING;
ALTER TABLE nw.payment ADD COLUMN recurrent_intention_token_source_payment_id CHARACTER VARYING;
ALTER TABLE nw.payment ADD COLUMN recurrent_intention_token CHARACTER VARYING;