ALTER TABLE nw.shop DROP CONSTRAINT shop_uniq
, ADD CONSTRAINT shop_uniq UNIQUE(party_id, shop_id, sequence_id, change_id, claim_effect_id);

ALTER TABLE nw.contract DROP CONSTRAINT contract_uniq
, ADD CONSTRAINT contract_uniq UNIQUE(party_id, contract_id, sequence_id, change_id, claim_effect_id);

ALTER TABLE nw.contractor DROP CONSTRAINT contractor_uniq
, ADD CONSTRAINT contractor_uniq UNIQUE(party_id, contractor_id, sequence_id, change_id, claim_effect_id);

ALTER TABLE nw.shop DROP COLUMN revision;
ALTER TABLE nw.contract DROP COLUMN revision;
ALTER TABLE nw.contractor DROP COLUMN revision;

CREATE TABLE nw.shop_revision(
  id                             BIGSERIAL NOT NULL,
  obj_id                         BIGINT NOT NULL,
  revision                       BIGINT NOT NULL,
  wtime                          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  CONSTRAINT shop_revision_pkey PRIMARY KEY (id)
);
CREATE UNIQUE INDEX shop_revision_idx on nw.shop_revision(obj_id, revision);

CREATE TABLE nw.contract_revision(
  id                             BIGSERIAL NOT NULL,
  obj_id                         BIGINT NOT NULL,
  revision                       BIGINT NOT NULL,
  wtime                          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  CONSTRAINT contract_revision_pkey PRIMARY KEY (id)
);
CREATE UNIQUE INDEX contract_revision_idx on nw.contract_revision(obj_id, revision);

CREATE TABLE nw.contractor_revision(
  id                             BIGSERIAL NOT NULL,
  obj_id                         BIGINT NOT NULL,
  revision                       BIGINT NOT NULL,
  wtime                          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  CONSTRAINT contractor_revision_pkey PRIMARY KEY (id)
);
CREATE UNIQUE INDEX contractor_revision_idx on nw.contractor_revision(obj_id, revision);