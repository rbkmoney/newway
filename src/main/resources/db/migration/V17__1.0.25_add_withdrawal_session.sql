/**
  * Создание необходимых структур для сессий в БД
  */

CREATE TYPE nw.BANK_CARD_PAYMENT_SYSTEM AS ENUM ('visa', 'mastercard', 'visaelectron', 'maestro',
                                                 'forbrugsforeningen', 'dankort', 'amex', 'dinersclub',
                                                 'discover', 'unionpay', 'jcb', 'nspkmir');

CREATE TYPE nw.WITHDRAWAL_SESSION_STATUS AS ENUM ('active', 'success', 'failed');

CREATE TABLE nw.withdrawal_session (
  id                                BIGSERIAL                    NOT NULL,
  event_id                          BIGINT                       NOT NULL,
  event_created_at                  TIMESTAMP WITHOUT TIME ZONE  NOT NULL,
  event_occured_at                  TIMESTAMP WITHOUT TIME ZONE  NOT NULL,
  sequence_id                       INT                          NOT NULL,
  withdrawal_session_id             CHARACTER VARYING            NOT NULL,
  withdrawal_session_status         WITHDRAWAL_SESSION_STATUS    NOT NULL,
  provider_id                       CHARACTER VARYING            NOT NULL,
  withdrawal_id                     CHARACTER VARYING            NOT NULL,
  destination_name                  CHARACTER VARYING            NOT NULL,
  destination_card_token            CHARACTER VARYING            NOT NULL,
  destination_card_payment_system   BANK_CARD_PAYMENT_SYSTEM     NULL,
  destination_card_bin              CHARACTER VARYING            NULL,
  destination_card_masked_pan       CHARACTER VARYING            NULL,
  amount                            BIGINT                       NOT NULL,
  currency_code                     CHARACTER VARYING            NOT NULL,
  sender_party_id                   CHARACTER VARYING            NOT NULL,
  sender_provider_id                CHARACTER VARYING            NOT NULL,
  sender_class_id                   CHARACTER VARYING            NOT NULL,
  sender_contract_id                CHARACTER VARYING            NULL,
  receiver_party_id                 CHARACTER VARYING            NOT NULL,
  receiver_provider_id              CHARACTER VARYING            NOT NULL,
  receiver_class_id                 CHARACTER VARYING            NOT NULL,
  receiver_contract_id              CHARACTER VARYING            NULL,
  adapter_state                     CHARACTER VARYING            NULL,
  tran_info_id                      CHARACTER VARYING            NULL,
  tran_info_timestamp               TIMESTAMP WITHOUT TIME ZONE  NULL,
  tran_info_json                    CHARACTER VARYING            NULL,
  wtime                             TIMESTAMP WITHOUT TIME ZONE  NOT NULL DEFAULT (now() at time zone 'utc'),
  current                           BOOLEAN                      NOT NULL DEFAULT TRUE,
  CONSTRAINT withdrawal_session_PK PRIMARY KEY (id)
);

CREATE INDEX withdrawal_session_event_id_idx         ON nw.withdrawal_session (event_id);
CREATE INDEX withdrawal_session_event_created_at_idx ON nw.withdrawal_session (event_created_at);
CREATE INDEX withdrawal_session_event_occured_at_idx ON nw.withdrawal_session (event_occured_at);
CREATE INDEX withdrawal_session_id_idx               ON nw.withdrawal_session (withdrawal_session_id);
