CREATE TYPE nw.chargeback_status AS ENUM ('pending', 'accepted', 'rejected', 'cancelled');

CREATE TYPE nw.chargeback_category AS ENUM ('fraud', 'dispute', 'authorisation', 'processing_error');

CREATE TYPE nw.chargeback_stage AS ENUM ('chargeback', 'pre_arbitration', 'arbitration');

CREATE TABLE nw.chargeback
(
    id                 BIGSERIAL                   NOT NULL,
    sequence_id        BIGINT                      NOT NULL,
    change_id          INT                         NOT NULL,
    domain_revision    BIGINT                      NOT NULL,
    party_revision     BIGINT,
    chargeback_id      CHARACTER VARYING           NOT NULL,
    payment_id         CHARACTER VARYING           NOT NULL,
    invoice_id         CHARACTER VARYING           NOT NULL,
    shop_id            CHARACTER VARYING           NOT NULL,
    party_id           CHARACTER VARYING           NOT NULL,
    external_id        CHARACTER VARYING,
    event_created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status             nw.chargeback_status        NOT NULL,
    levy_amount        BIGINT,
    levy_currency_code CHARACTER VARYING,
    amount             BIGINT,
    currency_code      CHARACTER VARYING,
    reason_code        CHARACTER VARYING,
    reason_category    nw.chargeback_category      NOT NULL,
    stage              nw.chargeback_stage         NOT NULL,
    current            BOOLEAN                     NOT NULL DEFAULT TRUE,
    context            BYTEA,
    wtime              TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),

    CONSTRAINT chargeback_pkey PRIMARY KEY (id)
);

ALTER TABLE nw.chargeback
    ADD CONSTRAINT chargeback_uniq UNIQUE (invoice_id, sequence_id, change_id);

CREATE INDEX chargeback_invoice_id on nw.chargeback (invoice_id);
CREATE INDEX chargeback_party_id on nw.chargeback (party_id);
CREATE INDEX chargeback_status on nw.chargeback (status);
CREATE INDEX chargeback_created_at on nw.chargeback (created_at);
