alter table nw.rate add column sequence_id bigint;
alter table nw.rate add column change_id int;

CREATE UNIQUE INDEX idx_uniq ON nw.rate(source_id, sequence_id, change_id, source_symbolic_code, destination_symbolic_code);

delete from nw.rate where event_id > 1339;