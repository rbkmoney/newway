alter table nw.destination add column resource_crypto_wallet_id character varying;
alter table nw.destination add column resource_crypto_wallet_type character varying;

alter table nw.payment add column payer_crypto_currency_type character varying;
create type nw.destination_resource_type as enum ('bank_card', 'crypto_wallet');
alter table nw.destination add column resource_type nw.destination_resource_type;
update nw.destination set resource_type = 'bank_card'::nw.destination_resource_type;