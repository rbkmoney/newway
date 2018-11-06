CREATE TABLE nw.identity (
  id                             BIGSERIAL                   NOT NULL,
  event_id                       BIGINT                      NOT NULL,
  event_created_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_occured_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  sequence_id                    INT                         NOT NULL,
  party_id                       CHARACTER VARYING           NOT NULL,
  party_contract_id              CHARACTER VARYING,
  identity_id                    CHARACTER VARYING           NOT NULL,
  identity_provider_id           CHARACTER VARYING           NOT NULL,
  identity_class_id              CHARACTER VARYING           NOT NULL,
  identity_effective_chalenge_id CHARACTER VARYING,
  identity_level_id              CHARACTER VARYING,
  wtime                          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                        BOOLEAN                     NOT NULL DEFAULT TRUE,
  CONSTRAINT identity_pkey PRIMARY KEY (id)
);

CREATE INDEX identity_event_id_idx on nw.identity(event_id);
CREATE INDEX identity_event_created_at_idx on nw.identity(event_created_at);
CREATE INDEX identity_event_occured_at_idx on nw.identity(event_occured_at);
CREATE INDEX identity_id_idx on nw.identity(identity_id);
CREATE INDEX identity_party_id_idx on nw.identity(party_id);

CREATE TYPE nw.withdrawal_status AS ENUM ('pending', 'succeeded', 'failed');
CREATE TYPE nw.withdrawal_transfer_status AS ENUM ('created', 'prepared', 'committed', 'cancelled');

CREATE TABLE nw.withdrawal (
  id                         BIGSERIAL                   NOT NULL,
  event_id                   BIGINT                      NOT NULL,
  event_created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_occured_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  sequence_id                INT                         NOT NULL,
  source_id                  CHARACTER VARYING           NOT NULL,
  destination_id             CHARACTER VARYING           NOT NULL,
  withdrawal_id              CHARACTER VARYING           NOT NULL,
  provider_id                CHARACTER VARYING,
  amount                     BIGINT                      NOT NULL,
  currency_code              CHARACTER VARYING           NOT NULL,
  withdrawal_status          nw.withdrawal_status        NOT NULL,
  withdrawal_transfer_status nw.withdrawal_transfer_status,
  wtime                      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                    BOOLEAN                     NOT NULL DEFAULT TRUE,
  CONSTRAINT withdrawal_pkey PRIMARY KEY (id)
);

CREATE INDEX withdrawal_event_id_idx on nw.withdrawal(event_id);
CREATE INDEX withdrawal_event_created_at_idx on nw.withdrawal(event_created_at);
CREATE INDEX withdrawal_event_occured_at_idx on nw.withdrawal(event_occured_at);
CREATE INDEX withdrawal_id_idx on nw.withdrawal(withdrawal_id);


CREATE TABLE nw.fistful_cash_flow (
  id                             BIGSERIAL            NOT NULL,
  obj_id                         BIGINT               NOT NULL,
  source_account_type            nw.cash_flow_account NOT NULL,
  source_account_type_value      CHARACTER VARYING    NOT NULL,
  source_account_id              CHARACTER VARYING    NOT NULL,
  destination_account_type       nw.cash_flow_account NOT NULL,
  destination_account_type_value CHARACTER VARYING    NOT NULL,
  destination_account_id         CHARACTER VARYING    NOT NULL,
  amount                         BIGINT               NOT NULL,
  currency_code                  CHARACTER VARYING    NOT NULL,
  details                        CHARACTER VARYING,
  CONSTRAINT fistful_cash_flow_pkey PRIMARY KEY (id)
);

CREATE INDEX fistful_cash_flow_obj_id_idx on nw.fistful_cash_flow(obj_id);

CREATE TYPE nw.challenge_status AS ENUM ('pending', 'cancelled', 'completed', 'failed');
CREATE TYPE nw.challenge_resolution AS ENUM ('approved', 'denied');

CREATE TABLE nw.challenge (
  id                    BIGSERIAL                   NOT NULL,
  event_id              BIGINT                      NOT NULL,
  event_created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_occured_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  sequence_id           INT                         NOT NULL,
  identity_id           CHARACTER VARYING           NOT NULL,
  challenge_id          CHARACTER VARYING           NOT NULL,
  challenge_class_id    CHARACTER VARYING           NOT NULL,
  challenge_status      nw.challenge_status         NOT NULL,
  challenge_resolution  nw.challenge_resolution,
  challenge_valid_until TIMESTAMP WITHOUT TIME ZONE,
  wtime                 TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current               BOOLEAN                     NOT NULL DEFAULT TRUE,
  CONSTRAINT challenge_pkey PRIMARY KEY (id)
);

CREATE INDEX challenge_event_id_idx on nw.challenge(event_id);
CREATE INDEX challenge_event_created_at_idx on nw.challenge(event_created_at);
CREATE INDEX challenge_event_occured_at_idx on nw.challenge(event_occured_at);
CREATE INDEX challenge_id_idx on nw.challenge(challenge_id);

CREATE TABLE nw.wallet (
  id               BIGSERIAL                   NOT NULL,
  event_id         BIGINT                      NOT NULL,
  event_created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_occured_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  sequence_id      INT                         NOT NULL,
  wallet_id        CHARACTER VARYING           NOT NULL,
  wallet_name      CHARACTER VARYING           NOT NULL,
  identity_id      CHARACTER VARYING,
  party_id         CHARACTER VARYING,
  currency_code    CHARACTER VARYING,
  wtime            TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current          BOOLEAN                     NOT NULL DEFAULT TRUE,
  CONSTRAINT wallet_pkey PRIMARY KEY (id)
);

CREATE INDEX wallet_event_id_idx on nw.wallet(event_id);
CREATE INDEX wallet_event_created_at_idx on nw.wallet(event_created_at);
CREATE INDEX wallet_event_occured_at_idx on nw.wallet(event_occured_at);
CREATE INDEX wallet_id_idx on nw.wallet(wallet_id);

