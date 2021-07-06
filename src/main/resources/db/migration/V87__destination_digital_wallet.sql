ALTER TABLE nw.destination ADD COLUMN resource_digital_wallet_id CHARACTER VARYING;
ALTER TABLE nw.destination ADD COLUMN resource_digital_wallet_data CHARACTER VARYING;

ALTER TABLE nw.withdrawal_session ADD COLUMN resource_digital_wallet_id CHARACTER VARYING;
ALTER TABLE nw.withdrawal_session ADD COLUMN resource_digital_wallet_data CHARACTER VARYING;
