ALTER TABLE nw.payment ALTER COLUMN payer_mobile_operator TYPE VARCHAR USING payer_mobile_operator::varchar;
ALTER TABLE nw.recurrent_payment_tool ALTER COLUMN mobile_commerce_operator TYPE VARCHAR USING mobile_commerce_operator::varchar;

DROP TYPE nw.mobile_operator_type;
