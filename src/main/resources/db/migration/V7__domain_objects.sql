DROP TABLE nw.category;
--category--
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

--currency--
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

--calendar--
CREATE TABLE nw.calendar(
  id                       BIGSERIAL NOT NULL,
  version_id               BIGINT NOT NULL,
  calendar_ref_id          INT NOT NULL,
  name                     CHARACTER VARYING NOT NULL,
  description              CHARACTER VARYING,
  timezone                 CHARACTER VARYING NOT NULL,
  holidays_json            CHARACTER VARYING NOT NULL,
  first_day_of_week        INT,
  wtime                    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                  BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT calendar_pkey PRIMARY KEY (id)
);

CREATE INDEX calendar_version_id on nw.calendar(version_id);
CREATE INDEX calendar_idx on nw.calendar(calendar_ref_id);

--provider--
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

--terminal--
CREATE TABLE nw.terminal(
  id                             BIGSERIAL NOT NULL,
  version_id                     BIGINT NOT NULL,
  terminal_ref_id                INT NOT NULL,
  name                           CHARACTER VARYING NOT NULL,
  description                    CHARACTER VARYING NOT NULL,
  options_json                   CHARACTER VARYING,
  risk_coverage                  CHARACTER VARYING NOT NULL,
  terms_json                     CHARACTER VARYING,
  wtime                          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                        BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT terminal_pkey PRIMARY KEY (id)
);

CREATE INDEX terminal_version_id on nw.terminal(version_id);
CREATE INDEX terminal_idx on nw.terminal(terminal_ref_id);

--payment_method--
CREATE TYPE nw.payment_method_type AS ENUM('bank_card', 'payment_terminal', 'digital_wallet', 'tokenized_bank_card');

CREATE TABLE nw.payment_method(
  id                             BIGSERIAL NOT NULL,
  version_id                     BIGINT NOT NULL,
  payment_method_ref_id          CHARACTER VARYING NOT NULL,
  name                           CHARACTER VARYING NOT NULL,
  description                    CHARACTER VARYING NOT NULL,
  type                           nw.payment_method_type NOT NULL,
  wtime                          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                        BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT payment_method_pkey PRIMARY KEY (id)
);

CREATE INDEX payment_method_version_id on nw.payment_method(version_id);
CREATE INDEX payment_method_idx on nw.payment_method(payment_method_ref_id);

--payout_method--
CREATE TABLE nw.payout_method(
  id                             BIGSERIAL NOT NULL,
  version_id                     BIGINT NOT NULL,
  payout_method_ref_id           CHARACTER VARYING NOT NULL,
  name                           CHARACTER VARYING NOT NULL,
  description                    CHARACTER VARYING NOT NULL,
  wtime                          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                        BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT payout_method_pkey PRIMARY KEY (id)
);

CREATE INDEX payout_method_version_id on nw.payout_method(version_id);
CREATE INDEX payout_method_idx on nw.payout_method(payout_method_ref_id);

--payment_institution--
CREATE TABLE nw.payment_institution(
  id                                    BIGSERIAL NOT NULL,
  version_id                            BIGINT NOT NULL,
  payment_institution_ref_id            INT NOT NULL,
  name                                  CHARACTER VARYING NOT NULL,
  description                           CHARACTER VARYING,
  calendar_ref_id                       INT,
  system_account_set_json               CHARACTER VARYING NOT NULL,
  default_contract_template_json        CHARACTER VARYING NOT NULL,
  default_wallet_contract_template_json CHARACTER VARYING,
  providers_json                        CHARACTER VARYING NOT NULL,
  inspector_json                        CHARACTER VARYING NOT NULL,
  realm                                 CHARACTER VARYING NOT NULL,
  residences_json                       CHARACTER VARYING NOT NULL,
  wtime                                 TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                               BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT payment_institution_pkey PRIMARY KEY (id)
);

CREATE INDEX payment_institution_version_id on nw.payment_institution(version_id);
CREATE INDEX payment_institution_idx on nw.payment_institution(payment_institution_ref_id);

--inspector--
CREATE TABLE nw.inspector(
  id                             BIGSERIAL NOT NULL,
  version_id                     BIGINT NOT NULL,
  inspector_ref_id               INT NOT NULL,
  name                           CHARACTER VARYING NOT NULL,
  description                    CHARACTER VARYING NOT NULL,
  proxy_ref_id                   INT NOT NULL,
  proxy_additional_json          CHARACTER VARYING NOT NULL,
  fallback_risk_score            CHARACTER VARYING,
  wtime                          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                        BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT inspector_pkey PRIMARY KEY (id)
);

CREATE INDEX inspector_version_id on nw.inspector(version_id);
CREATE INDEX inspector_idx on nw.inspector(inspector_ref_id);

--proxy--
CREATE TABLE nw.proxy(
  id                             BIGSERIAL NOT NULL,
  version_id                     BIGINT NOT NULL,
  proxy_ref_id                   INT NOT NULL,
  name                           CHARACTER VARYING NOT NULL,
  description                    CHARACTER VARYING NOT NULL,
  url                            CHARACTER VARYING NOT NULL,
  options_json                   CHARACTER VARYING NOT NULL,
  wtime                          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                        BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT proxy_pkey PRIMARY KEY (id)
);

CREATE INDEX proxy_version_id on nw.proxy(version_id);
CREATE INDEX proxy_idx on nw.proxy(proxy_ref_id);

--term_set_hierarchy--
CREATE TABLE nw.term_set_hierarchy(
  id                             BIGSERIAL NOT NULL,
  version_id                     BIGINT NOT NULL,
  term_set_hierarchy_ref_id      INT NOT NULL,
  name                           CHARACTER VARYING,
  description                    CHARACTER VARYING,
  parent_terms_ref_id            INT,
  term_sets_json                 CHARACTER VARYING NOT NULL,
  wtime                          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                        BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT term_set_hierarchy_pkey PRIMARY KEY (id)
);

CREATE INDEX term_set_hierarchy_version_id on nw.term_set_hierarchy(version_id);
CREATE INDEX term_set_hierarchy_idx on nw.term_set_hierarchy(term_set_hierarchy_ref_id);