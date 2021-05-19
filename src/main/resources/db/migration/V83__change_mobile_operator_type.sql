alter table nw.recurrent_payment_tool rename column mobile_commerce_operator to mobile_commerce_operator_legacy;
alter table nw.recurrent_payment_tool add column mobile_commerce_operator character varying;

alter table nw.payment rename column payer_mobile_operator to payer_mobile_operator_legacy;
alter table nw.payment add column payer_mobile_operator character varying;
