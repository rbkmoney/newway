drop index deposit_event_id_idx;
ALTER TABLE nw.deposit DROP COLUMN event_id;
ALTER TABLE nw.deposit ADD COLUMN change_id INT not null;
ALTER TABLE nw.deposit ADD CONSTRAINT deposit_uniq UNIQUE(deposit_id, sequence_id, change_id);

drop INDEX destination_event_id_idx;
ALTER TABLE nw.destination DROP COLUMN event_id;
ALTER TABLE nw.destination ADD CONSTRAINT destination_uniq UNIQUE(destination_id, sequence_id);

drop INDEX identity_event_id_idx;
ALTER TABLE nw.identity DROP COLUMN event_id;
ALTER TABLE nw.identity ADD CONSTRAINT identity_uniq UNIQUE(identity_id, sequence_id);

drop INDEX challenge_event_id_idx;
ALTER TABLE nw.challenge DROP COLUMN event_id;
ALTER TABLE nw.challenge ADD CONSTRAINT challenge_uniq UNIQUE(challenge_id, identity_id, sequence_id);

drop INDEX source_event_id_idx;
ALTER TABLE nw.source DROP COLUMN event_id;
ALTER TABLE nw.source ADD CONSTRAINT source_uniq UNIQUE(source_id, sequence_id);

drop INDEX wallet_event_id_idx;
ALTER TABLE nw.wallet DROP COLUMN event_id;
ALTER TABLE nw.wallet ADD COLUMN change_id INT not null;
ALTER TABLE nw.wallet ADD CONSTRAINT wallet_uniq UNIQUE(wallet_id, sequence_id, change_id);

drop INDEX withdrawal_event_id_idx;
ALTER TABLE nw.withdrawal DROP COLUMN event_id;
ALTER TABLE nw.withdrawal ADD CONSTRAINT withdrawal_uniq UNIQUE(withdrawal_id, sequence_id);

drop INDEX withdrawal_session_event_id_idx;
ALTER TABLE nw.withdrawal_session DROP COLUMN event_id;
ALTER TABLE nw.withdrawal_session ADD COLUMN change_id INT not null;
ALTER TABLE nw.withdrawal_session ADD CONSTRAINT withdrawal_session_uniq UNIQUE(withdrawal_session_id, sequence_id, change_id);