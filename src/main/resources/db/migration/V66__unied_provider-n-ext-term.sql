ALTER TABLE nw.provider ADD identity CHARACTER VARYING;
ALTER TABLE nw.provider ADD wallet_terms_json CHARACTER VARYING;
ALTER TABLE nw.provider ADD params_schema_json CHARACTER VARYING;

ALTER TABLE nw.terminal ADD external_terminal_id CHARACTER VARYING;
ALTER TABLE nw.terminal ADD external_merchant_id CHARACTER VARYING;
ALTER TABLE nw.terminal ADD mcc CHARACTER VARYING;
