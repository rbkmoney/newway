CREATE TYPE nw.session_target_status AS ENUM('processed', 'captured', 'cancelled', 'refunded');
CREATE TYPE nw.session_change_payload AS ENUM('session_started', 'session_finished', 'session_suspended', 'session_activated', 'session_transaction_bound', 'session_proxy_state_changed', 'session_interaction_requested');
CREATE TYPE nw.session_change_payload_finished_result AS ENUM('succeeded', 'failed');

ALTER TABLE nw.payment ADD COLUMN session_target nw.session_target_status;
ALTER TABLE nw.payment ADD COLUMN session_payload nw.session_change_payload;
ALTER TABLE nw.payment ADD COLUMN session_payload_finished_result nw.session_change_payload_finished_result;
ALTER TABLE nw.payment ADD COLUMN session_payload_finished_result_failed_failure_json CHARACTER VARYING;
ALTER TABLE nw.payment ADD COLUMN session_payload_suspended_tag CHARACTER VARYING;
ALTER TABLE nw.payment ADD COLUMN session_payload_transaction_bound_trx_id CHARACTER VARYING;
ALTER TABLE nw.payment ADD COLUMN session_payload_transaction_bound_trx_timestamp TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE nw.payment ADD COLUMN session_payload_transaction_bound_trx_extra_json CHARACTER VARYING;
ALTER TABLE nw.payment ADD COLUMN session_payload_proxy_state_changed_proxy_state BYTEA;
ALTER TABLE nw.payment ADD COLUMN session_payload_interaction_requested_interaction_json CHARACTER VARYING;

ALTER TABLE nw.refund ADD COLUMN session_target nw.session_target_status;
ALTER TABLE nw.refund ADD COLUMN session_payload nw.session_change_payload;
ALTER TABLE nw.refund ADD COLUMN session_payload_finished_result nw.session_change_payload_finished_result;
ALTER TABLE nw.refund ADD COLUMN session_payload_finished_result_failed_failure_json CHARACTER VARYING;
ALTER TABLE nw.refund ADD COLUMN session_payload_suspended_tag CHARACTER VARYING;
ALTER TABLE nw.refund ADD COLUMN session_payload_transaction_bound_trx_id CHARACTER VARYING;
ALTER TABLE nw.refund ADD COLUMN session_payload_transaction_bound_trx_timestamp TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE nw.refund ADD COLUMN session_payload_transaction_bound_trx_extra_json CHARACTER VARYING;
ALTER TABLE nw.refund ADD COLUMN session_payload_proxy_state_changed_proxy_state BYTEA;
ALTER TABLE nw.refund ADD COLUMN session_payload_interaction_requested_interaction_json CHARACTER VARYING;