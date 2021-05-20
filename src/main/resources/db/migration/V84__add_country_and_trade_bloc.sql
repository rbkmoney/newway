--contractor--
ALTER TABLE nw.contractor
    ADD COLUMN international_legal_entity_country_code character varying;

--trade_bloc--
CREATE TABLE nw.trade_bloc
(
    id                BIGSERIAL         NOT NULL,
    version_id        BIGINT            NOT NULL,
    trade_bloc_ref_id CHARACTER VARYING NOT NULL,
    name              CHARACTER VARYING NOT NULL,
    description       CHARACTER VARYING NOT NULL,
    wtime             TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
    current           BOOLEAN           NOT NULL DEFAULT TRUE,
    CONSTRAINT trade_bloc_pkey PRIMARY KEY (id)
);

--country--
CREATE TABLE nw.country
(
    id             BIGSERIAL         NOT NULL,
    version_id     BIGINT            NOT NULL,
    country_ref_id CHARACTER VARYING NOT NULL,
    name           CHARACTER VARYING NOT NULL,
    trade_bloc     TEXT[] NOT NULL,
    wtime          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
    current        BOOLEAN           NOT NULL DEFAULT TRUE,
    CONSTRAINT country_pkey PRIMARY KEY (id)
);
