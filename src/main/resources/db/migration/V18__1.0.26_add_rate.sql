CREATE TABLE nw.rate (
  id                        BIGSERIAL                   NOT NULL,
  event_id                  BIGINT                      NOT NULL,
  event_created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_source_id           CHARACTER VARYING           NOT NULL,
  lower_bound_inclusive     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  upper_bound_exclusive     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  source_symbolic_code      CHARACTER VARYING           NOT NULL,
  source_exponent           SMALLINT                    NOT NULL,
  destination_symbolic_code CHARACTER VARYING           NOT NULL,
  destination_exponent      SMALLINT                    NOT NULL,
  exchange_rate_rational_p  BIGINT                      NOT NULL,
  exchange_rate_rational_q  BIGINT                      NOT NULL,
  wtime                     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                   BOOLEAN                     NOT NULL DEFAULT TRUE,
  CONSTRAINT rate_pkey PRIMARY KEY (id)
);

CREATE INDEX rate_event_id_idx
  ON nw.rate (event_id);
CREATE INDEX rate_event_created_at_idx
  ON nw.rate (event_created_at);
CREATE INDEX rate_event_source_id_idx
  ON nw.rate (event_source_id);
