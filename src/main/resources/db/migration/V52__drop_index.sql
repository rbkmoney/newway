DROP INDEX party_event_id;
ALTER TABLE nw.party DROP COLUMN event_id;

DROP INDEX shop_event_id;
ALTER TABLE nw.shop DROP COLUMN event_id;

DROP INDEX contract_event_id;
ALTER TABLE nw.contract DROP COLUMN event_id;

DROP INDEX contractor_event_id;
ALTER TABLE nw.contractor DROP COLUMN event_id;