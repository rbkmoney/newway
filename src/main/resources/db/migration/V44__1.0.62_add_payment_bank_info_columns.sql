alter table nw.payment add column if not exists payer_issuer_country character varying;
alter table nw.payment add column if not exists payer_bank_name character varying;
