ALTER TABLE nw.invoice ADD COLUMN sequence_id BIGINT;
ALTER TABLE nw.invoice ADD COLUMN change_id INT;
ALTER TABLE nw.invoice DROP COLUMN event_id;
ALTER TABLE nw.invoice ADD CONSTRAINT invoice_uniq UNIQUE (invoice_id, sequence_id, change_id);

ALTER TABLE nw.payment ADD COLUMN sequence_id BIGINT;
ALTER TABLE nw.payment ADD COLUMN change_id INT;
ALTER TABLE nw.payment DROP COLUMN event_id;
ALTER TABLE nw.payment ADD CONSTRAINT payment_uniq UNIQUE (invoice_id, sequence_id, change_id);

ALTER TABLE nw.refund ADD COLUMN sequence_id BIGINT;
ALTER TABLE nw.refund ADD COLUMN change_id INT;
ALTER TABLE nw.refund DROP COLUMN event_id;
ALTER TABLE nw.refund ADD CONSTRAINT refund_uniq UNIQUE (invoice_id, sequence_id, change_id);

ALTER TABLE nw.adjustment ADD COLUMN sequence_id BIGINT;
ALTER TABLE nw.adjustment ADD COLUMN change_id INT;
ALTER TABLE nw.adjustment DROP COLUMN event_id;
ALTER TABLE nw.adjustment ADD CONSTRAINT adjustment_uniq UNIQUE (invoice_id, sequence_id, change_id);
