alter table nw.invoice add column if not exists external_id character varying;
create index if not exists invoice_external_id_idx on nw.invoice(external_id) where external_id is not null;

alter table nw.payment add column if not exists external_id character varying;
create index  if not exists payment_external_id_idx on nw.payment(external_id) where external_id is not null;

alter table nw.refund add column if not exists external_id character varying;
create index if not exists refund_external_id_idx on nw.refund(external_id) where external_id is not null;
