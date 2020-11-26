alter table nw.payment_routing_rule add column version_id bigint not null;
alter table nw.payment_routing_rule rename column rule_id to rule_ref_id;
