alter table nw.destination alter column resource_bank_card_token drop not null;
alter table nw.destination alter column resource_type set not null;
alter table nw.withdrawal_session alter column destination_card_token drop not null;
alter table nw.withdrawal_session add column resource_type nw.destination_resource_type;
update nw.withdrawal_session set resource_type = 'bank_card'::nw.destination_resource_type;
alter table nw.withdrawal_session alter column resource_type set not null;
alter table nw.withdrawal_session add column resource_crypto_wallet_id character varying;
alter table nw.withdrawal_session add column resource_crypto_wallet_type character varying;