CREATE SEQUENCE nw.inv_seq
    INCREMENT 1
    START 200000000
    MINVALUE 200000000
    CACHE 1;

CREATE SEQUENCE nw.pmnt_seq
    INCREMENT 1
    START 600000000
    MINVALUE 600000000
    CACHE 1;

alter table nw.payment add column capture_started_params_cart_json character varying;
alter table nw.invoice_cart drop constraint if exists fk_cart_to_invoice;