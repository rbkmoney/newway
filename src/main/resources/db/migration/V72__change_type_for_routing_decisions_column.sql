alter table nw.payment_routing_rule drop column routing_decisions_jsonb;
alter table nw.payment_routing_rule add column routing_decisions_json character varying not null;
