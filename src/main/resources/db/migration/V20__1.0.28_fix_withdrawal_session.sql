
 ALTER TABLE nw.withdrawal_session ALTER COLUMN sender_party_id      DROP NOT NULL;
 ALTER TABLE nw.withdrawal_session ALTER COLUMN sender_provider_id   DROP NOT NULL;
 ALTER TABLE nw.withdrawal_session ALTER COLUMN sender_class_id      DROP NOT NULL;
 ALTER TABLE nw.withdrawal_session ALTER COLUMN receiver_party_id    DROP NOT NULL;
 ALTER TABLE nw.withdrawal_session ALTER COLUMN receiver_provider_id DROP NOT NULL;
 ALTER TABLE nw.withdrawal_session ALTER COLUMN receiver_class_id    DROP NOT NULL;