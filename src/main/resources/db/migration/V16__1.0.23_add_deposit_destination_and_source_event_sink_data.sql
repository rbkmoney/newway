-- clear previous data
delete
from nw.wallet;
delete
from nw.withdrawal;
delete
from nw.identity;
delete
from nw.fistful_cash_flow;

alter table nw.wallet
  add column account_id character varying;
alter table nw.wallet
  add column accounter_account_id bigint;
alter table nw.withdrawal
  add column fee bigint;
alter table nw.withdrawal
  add column provider_fee bigint;
alter table nw.withdrawal
  rename column source_id to wallet_id;

CREATE TYPE nw.fistful_cash_flow_change_type AS ENUM ('withdrawal', 'deposit');
alter table nw.fistful_cash_flow
  add column obj_type nw.fistful_cash_flow_change_type not null;


CREATE TYPE nw.source_status AS ENUM ('authorized', 'unauthorized');

CREATE TABLE nw.source (
  id                        BIGSERIAL                   NOT NULL,
  event_id                  BIGINT                      NOT NULL,
  event_created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_occured_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  sequence_id               INT                         NOT NULL,
  source_id                 CHARACTER VARYING           NOT NULL,
  source_name               CHARACTER VARYING           NOT NULL,
  source_status             nw.source_status            NOT NULL,
  resource_internal_details CHARACTER VARYING,
  account_id                CHARACTER VARYING,
  identity_id               CHARACTER VARYING,
  party_id                  CHARACTER VARYING,
  accounter_account_id      BIGINT,
  currency_code             CHARACTER VARYING,
  wtime                     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                   BOOLEAN                     NOT NULL DEFAULT TRUE,
  CONSTRAINT source_pkey PRIMARY KEY (id)
);

CREATE INDEX source_event_id_idx
  on nw.source (event_id);
CREATE INDEX source_event_created_at_idx
  on nw.source (event_created_at);
CREATE INDEX source_event_occured_at_idx
  on nw.source (event_occured_at);
CREATE INDEX source_id_idx
  on nw.source (source_id);

CREATE TYPE nw.destination_status AS ENUM ('authorized', 'unauthorized');

CREATE TABLE nw.destination (
  id                                BIGSERIAL                   NOT NULL,
  event_id                          BIGINT                      NOT NULL,
  event_created_at                  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_occured_at                  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  sequence_id                       INT                         NOT NULL,
  destination_id                    CHARACTER VARYING           NOT NULL,
  destination_name                  CHARACTER VARYING           NOT NULL,
  destination_status                nw.destination_status       NOT NULL,
  resource_bank_card_token          CHARACTER VARYING           NOT NULL,
  resource_bank_card_payment_system CHARACTER VARYING,
  resource_bank_card_bin            CHARACTER VARYING,
  resource_bank_card_masked_pan     CHARACTER VARYING,
  account_id                        CHARACTER VARYING,
  identity_id                       CHARACTER VARYING,
  party_id                          CHARACTER VARYING,
  accounter_account_id              BIGINT,
  currency_code                     CHARACTER VARYING,
  wtime                             TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                           BOOLEAN                     NOT NULL DEFAULT TRUE,
  CONSTRAINT destination_pkey PRIMARY KEY (id)
);

CREATE INDEX destination_event_id_idx
  on nw.destination (event_id);
CREATE INDEX destination_event_created_at_idx
  on nw.destination (event_created_at);
CREATE INDEX destination_event_occured_at_idx
  on nw.destination (event_occured_at);
CREATE INDEX destination_id_idx
  on nw.destination (destination_id);

CREATE TYPE nw.deposit_status AS ENUM ('pending', 'succeeded', 'failed');
CREATE TYPE nw.deposit_transfer_status AS ENUM ('created', 'prepared', 'committed', 'cancelled');

CREATE TABLE nw.deposit (
  id                      BIGSERIAL                   NOT NULL,
  event_id                BIGINT                      NOT NULL,
  event_created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_occured_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  sequence_id             INT                         NOT NULL,
  source_id               CHARACTER VARYING           NOT NULL,
  wallet_id               CHARACTER VARYING           NOT NULL,
  deposit_id              CHARACTER VARYING           NOT NULL,
  amount                  BIGINT                      NOT NULL,
  fee                     BIGINT,
  provider_fee            BIGINT,
  currency_code           CHARACTER VARYING           NOT NULL,
  deposit_status          nw.deposit_status           NOT NULL,
  deposit_transfer_status nw.deposit_transfer_status,
  wtime                   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                 BOOLEAN                     NOT NULL DEFAULT TRUE,
  CONSTRAINT deposit_pkey PRIMARY KEY (id)
);

CREATE INDEX deposit_event_id_idx
  on nw.deposit (event_id);
CREATE INDEX deposit_event_created_at_idx
  on nw.deposit (event_created_at);
CREATE INDEX deposit_event_occured_at_idx
  on nw.deposit (event_occured_at);
CREATE INDEX deposit_id_idx
  on nw.deposit (deposit_id);

