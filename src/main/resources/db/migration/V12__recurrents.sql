ALTER TABLE nw.payment ADD COLUMN make_recurrent BOOL;
ALTER TABLE nw.payment ADD COLUMN payer_recurrent_parent_invoice_id CHARACTER VARYING;
ALTER TABLE nw.payment ADD COLUMN payer_recurrent_parent_payment_id CHARACTER VARYING;
ALTER TABLE nw.payment ADD COLUMN recurrent_intention_token CHARACTER VARYING;