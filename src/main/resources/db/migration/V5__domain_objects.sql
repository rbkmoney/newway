CREATE TABLE nw.category(
  id                       BIGSERIAL NOT NULL,
  version_id               BIGINT NOT NULL,
  category_id              INT NOT NULL,
  name                     CHARACTER VARYING NOT NULL,
  description              CHARACTER VARYING NOT NULL,
  type                     CHARACTER VARYING,
  wtime                    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                  BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT category_pkey PRIMARY KEY (id)
);
