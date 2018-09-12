CREATE TABLE nw.category(
  id                       BIGSERIAL NOT NULL,
  version_id               BIGINT NOT NULL,
  category_ref_id          INT NOT NULL,
  name                     CHARACTER VARYING NOT NULL,
  description              CHARACTER VARYING NOT NULL,
  type                     CHARACTER VARYING,
  wtime                    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                  BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT category_pkey PRIMARY KEY (id)
);

CREATE INDEX category_version_id on nw.category(version_id);
CREATE INDEX category_idx on nw.category(category_ref_id);

CREATE TABLE nw.currency(
  id                       BIGSERIAL NOT NULL,
  version_id               BIGINT NOT NULL,
  currency_ref_id          CHARACTER VARYING NOT NULL,
  name                     CHARACTER VARYING NOT NULL,
  symbolic_code            CHARACTER VARYING NOT NULL,
  numeric_code             SMALLINT NOT NULL,
  exponent                 SMALLINT NOT NULL,
  wtime                    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                  BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT currency_pkey PRIMARY KEY (id)
);

CREATE INDEX currency_version_id on nw.currency(version_id);
CREATE INDEX currency_idx on nw.currency(currency_ref_id);

CREATE TABLE nw.calendar(
  id                       BIGSERIAL NOT NULL,
  version_id               BIGINT NOT NULL,
  calendar_ref_id          INT NOT NULL,
  name                     CHARACTER VARYING NOT NULL,
  description              CHARACTER VARYING NOT NULL,
  timezone                 TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  holidays_json            CHARACTER VARYING NOT NULL,
  first_day_of_week        INT,
  wtime                    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                  BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT calendar_pkey PRIMARY KEY (id)
);

CREATE INDEX calendar_version_id on nw.calendar(version_id);
CREATE INDEX calendar_idx on nw.calendar(calendar_ref_id);

CREATE TABLE nw.provider(
  id                             BIGSERIAL NOT NULL,
  version_id                     BIGINT NOT NULL,
  provider_ref_id                INT NOT NULL,
  name                           CHARACTER VARYING NOT NULL,
  description                    CHARACTER VARYING NOT NULL,
  proxy_ref_id                   INT NOT NULL,
  proxy_additional_json          CHARACTER VARYING NOT NULL,
  terminal_json                  CHARACTER VARYING NOT NULL,
  abs_account                    CHARACTER VARYING NOT NULL,
  payment_terms_json             CHARACTER VARYING,
  recurrent_paytool_terms_json   CHARACTER VARYING,
  accounts_json                  CHARACTER VARYING,
  wtime                          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                        BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT provider_pkey PRIMARY KEY (id)
);

CREATE INDEX provider_version_id on nw.provider(version_id);
CREATE INDEX provider_idx on nw.provider(provider_ref_id);

CREATE TABLE nw.terminal(
  id                             BIGSERIAL NOT NULL,
  version_id                     BIGINT NOT NULL,
  terminal_ref_id                INT NOT NULL,
  name                           CHARACTER VARYING NOT NULL,
  description                    CHARACTER VARYING NOT NULL,
  options_json                   CHARACTER VARYING,
  risk_coverage                  INT NOT NULL,
  terms_json                     CHARACTER VARYING,
  wtime                          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                        BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT terminal_pkey PRIMARY KEY (id)
);

CREATE INDEX terminal_version_id on nw.terminal(version_id);
CREATE INDEX terminal_idx on nw.terminal(terminal_ref_id);