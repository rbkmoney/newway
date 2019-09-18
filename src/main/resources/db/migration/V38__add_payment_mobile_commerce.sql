create type mobile_operator_type as enum ('mts', 'beeline', 'megafone', 'tele2', 'yota');

alter table nw.payment add column payer_mobile_operator nw.mobile_operator_type;

alter table nw.payment add column payer_mobile_phone_cc character varying;

alter table nw.payment add column payer_mobile_phone_ctn character varying;
