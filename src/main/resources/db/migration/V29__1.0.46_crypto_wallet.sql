alter table nw.destination add column resource_crypto_wallet_id character varying;
alter table nw.destination add column resource_crypto_wallet_type character varying;

alter table nw.payment add column payer_crypto_currency_type character varying;