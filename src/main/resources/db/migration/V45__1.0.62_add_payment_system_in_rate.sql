alter table nw.rate add column payment_system character varying not null default '';

drop index idx_uniq;
create unique index rate_ukey on nw.rate(source_id, sequence_id, change_id, source_symbolic_code, destination_symbolic_code, payment_system);