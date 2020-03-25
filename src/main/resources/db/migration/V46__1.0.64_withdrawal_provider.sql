--provider--
CREATE TABLE nw.withdrawal_provider(
  id                             BIGSERIAL NOT NULL,
  version_id                     BIGINT NOT NULL,
  withdrawal_provider_ref_id     INT NOT NULL,
  name                           CHARACTER VARYING NOT NULL,
  description                    CHARACTER VARYING,
  proxy_ref_id                   INT NOT NULL,
  proxy_additional_json          CHARACTER VARYING NOT NULL,
  identity                       CHARACTER VARYING,
  withdrawal_terms_json          CHARACTER VARYING,
  accounts_json                  CHARACTER VARYING,
  wtime                          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                        BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT withdrawal_provider_pkey PRIMARY KEY (id)
);

CREATE INDEX withdrawal_provider_version_id on nw.withdrawal_provider(version_id);
CREATE INDEX withdrawal_provider_idx on nw.withdrawal_provider(withdrawal_provider_ref_id);