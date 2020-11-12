CREATE TABLE nw.payment_routing_rule (
  id                       BIGSERIAL           NOT NULL,
  rule_id                  INTEGER             NOT NULL,
  name                     CHARACTER VARYING   NOT NULL,
  description              CHARACTER VARYING,
  routing_decisions_jsonb  JSONB               NOT NULL,
  wtime                    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                  BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT payment_routing_rule_pkey PRIMARY KEY (id)
);

CREATE INDEX payment_routing_rule_ref_id on nw.payment_routing_rule(rule_id);