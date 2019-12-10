alter table nw.invoice add column external_id character varying;
create index invoice_external_id_idx on nw.invoice(external_id) where external_id is not null;

alter table nw.payment add column external_id character varying;
create index payment_external_id_idx on nw.payment(external_id) where external_id is not null;

alter table nw.refund add column external_id character varying;
create index refund_external_id_idx on nw.refund(external_id) where external_id is not null;
