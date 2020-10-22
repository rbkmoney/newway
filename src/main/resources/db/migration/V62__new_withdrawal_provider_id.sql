alter table nw.withdrawal_session rename column provider_id to provider_id_legacy;
alter table nw.withdrawal_session alter column provider_id_legacy drop not null;
alter table nw.withdrawal_session add column provider_id int;

alter table nw.withdrawal rename column provider_id to provider_id_legacy;
alter table nw.withdrawal add column provider_id int;
