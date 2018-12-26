truncate nw.payout cascade;

alter table nw.payout add column amount bigint;
alter table nw.payout add column fee bigint;
alter table nw.payout add column currency_code character varying;

alter table nw.payout add column wallet_id character varying;
