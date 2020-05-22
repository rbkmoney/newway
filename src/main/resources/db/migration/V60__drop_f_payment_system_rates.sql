drop index rate_ukey;
ALTER TABLE nw.rate DROP COLUMN payment_system;

create unique index rate_ukey on nw.rate(source_id, sequence_id, change_id, source_symbolic_code, destination_symbolic_code);

drop index rate_event_id_idx;
ALTER TABLE nw.rate DROP COLUMN event_id;