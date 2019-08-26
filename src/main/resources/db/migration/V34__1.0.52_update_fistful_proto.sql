alter table nw.destination add column resource_crypto_wallet_data character varying;
alter table nw.destination add column resource_bank_card_type character varying;
alter table nw.destination add column resource_bank_card_issuer_country character varying;
alter table nw.destination add column resource_bank_card_bank_name character varying;

alter table nw.withdrawal_session add column resource_crypto_wallet_data character varying;
alter table nw.withdrawal_session add column resource_bank_card_type character varying;
alter table nw.withdrawal_session add column resource_bank_card_issuer_country character varying;
alter table nw.withdrawal_session add column resource_bank_card_bank_name character varying;
alter table nw.withdrawal_session add column tran_additional_info character varying;
alter table nw.withdrawal_session add column tran_additional_info_rrn character varying;
alter table nw.withdrawal_session add column tran_additional_info_json character varying;

alter table nw.withdrawal add column withdrawal_status_failed_failure_json character varying;

alter table nw.withdrawal_session drop column destination_name;
