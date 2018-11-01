ALTER TABLE nw.payment DROP COLUMN session_target;
ALTER TABLE nw.payment DROP COLUMN session_payload;
ALTER TABLE nw.payment DROP COLUMN session_payload_finished_result;
ALTER TABLE nw.payment DROP COLUMN session_payload_finished_result_failed_failure_json;
ALTER TABLE nw.payment DROP COLUMN session_payload_suspended_tag;
ALTER TABLE nw.payment DROP COLUMN session_payload_transaction_bound_trx_timestamp;
ALTER TABLE nw.payment DROP COLUMN session_payload_proxy_state_changed_proxy_state;
ALTER TABLE nw.payment DROP COLUMN session_payload_interaction_requested_interaction_json;

ALTER TABLE nw.refund DROP COLUMN session_target;
ALTER TABLE nw.refund DROP COLUMN session_payload;
ALTER TABLE nw.refund DROP COLUMN session_payload_finished_result;
ALTER TABLE nw.refund DROP COLUMN session_payload_finished_result_failed_failure_json;
ALTER TABLE nw.refund DROP COLUMN session_payload_suspended_tag;
ALTER TABLE nw.refund DROP COLUMN session_payload_transaction_bound_trx_timestamp;
ALTER TABLE nw.refund DROP COLUMN session_payload_proxy_state_changed_proxy_state;
ALTER TABLE nw.refund DROP COLUMN session_payload_interaction_requested_interaction_json;

DROP TYPE IF EXISTS nw.session_target_status;
DROP TYPE IF EXISTS nw.session_change_payload;
DROP TYPE IF EXISTS nw.session_change_payload_finished_result;