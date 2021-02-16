CREATE TYPE nw.deposit_revert_status AS ENUM ('pending', 'succeeded', 'failed');
CREATE TYPE nw.deposit_adjustment_status AS ENUM ('pending', 'succeeded');

CREATE TABLE nw.deposit_revert (
  id                      BIGSERIAL                   NOT NULL,
  event_created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_occured_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  sequence_id             INT                         NOT NULL,
  source_id               CHARACTER VARYING           NOT NULL,
  wallet_id               CHARACTER VARYING           NOT NULL,
  deposit_id              CHARACTER VARYING           NOT NULL,
  revert_id               CHARACTER VARYING           NOT NULL,
  amount                  BIGINT                      NOT NULL,
  fee                     BIGINT,
  provider_fee            BIGINT,
  currency_code           CHARACTER VARYING           NOT NULL,
  status                  nw.deposit_revert_status    NOT NULL,
  transfer_status         nw.deposit_transfer_status,
  reason                  CHARACTER VARYING,
  external_id             CHARACTER VARYING,
  wtime                   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                 BOOLEAN                     NOT NULL DEFAULT TRUE,
  CONSTRAINT deposit_revert_pkey PRIMARY KEY (id)
);

CREATE INDEX deposit_revert_event_created_at_idx on nw.deposit_revert (event_created_at);
CREATE INDEX deposit_revert_id_idx on nw.deposit_revert (deposit_id);
ALTER TABLE nw.deposit_revert ADD CONSTRAINT deposit_revert_uniq UNIQUE(deposit_id, revert_id, sequence_id);


CREATE TABLE nw.deposit_adjustment (
  id                      BIGSERIAL                    NOT NULL,
  event_created_at        TIMESTAMP WITHOUT TIME ZONE  NOT NULL,
  event_occured_at        TIMESTAMP WITHOUT TIME ZONE  NOT NULL,
  sequence_id             INT                          NOT NULL,
  source_id               CHARACTER VARYING            NOT NULL,
  wallet_id               CHARACTER VARYING            NOT NULL,
  deposit_id              CHARACTER VARYING            NOT NULL,
  adjustment_id           CHARACTER VARYING            NOT NULL,
  amount                  BIGINT                       NOT NULL,
  fee                     BIGINT,
  provider_fee            BIGINT,
  currency_code           CHARACTER VARYING            NOT NULL,
  status                  nw.deposit_adjustment_status NOT NULL,
  transfer_status         nw.deposit_transfer_status,
  deposit_status          nw.deposit_status,
  external_id             CHARACTER VARYING,
  wtime                   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                 BOOLEAN                     NOT NULL DEFAULT TRUE,
  CONSTRAINT deposit_adjustment_pkey PRIMARY KEY (id)
);

CREATE INDEX deposit_adjustment_event_created_at_idx on nw.deposit_adjustment (event_created_at);
CREATE INDEX deposit_adjustment_id_idx on nw.deposit_adjustment (deposit_id);
ALTER TABLE nw.deposit_adjustment ADD CONSTRAINT deposit_adjustment_uniq UNIQUE(deposit_id, adjustment_id, sequence_id);

