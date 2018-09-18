ALTER TABLE nw.payout_tool
  ADD COLUMN payout_tool_info_international_bank_number CHARACTER VARYING;
ALTER TABLE nw.payout_tool
  ADD COLUMN payout_tool_info_international_bank_aba_rtn CHARACTER VARYING;
ALTER TABLE nw.payout_tool
  ADD COLUMN payout_tool_info_international_bank_country_code CHARACTER VARYING;

ALTER TABLE nw.payout_tool
  ADD COLUMN payout_tool_info_international_correspondent_bank_account CHARACTER VARYING;
ALTER TABLE nw.payout_tool
  ADD COLUMN payout_tool_info_international_correspondent_bank_name CHARACTER VARYING;
ALTER TABLE nw.payout_tool
  ADD COLUMN payout_tool_info_international_correspondent_bank_address CHARACTER VARYING;
ALTER TABLE nw.payout_tool
  ADD COLUMN payout_tool_info_international_correspondent_bank_bic CHARACTER VARYING;
ALTER TABLE nw.payout_tool
  ADD COLUMN payout_tool_info_international_correspondent_bank_iban CHARACTER VARYING;
  ALTER TABLE nw.payout_tool
  ADD COLUMN payout_tool_info_international_correspondent_bank_number CHARACTER VARYING;
ALTER TABLE nw.payout_tool
  ADD COLUMN payout_tool_info_international_correspondent_bank_aba_rtn CHARACTER VARYING;
ALTER TABLE nw.payout_tool
  ADD COLUMN payout_tool_info_international_correspondent_bank_country_code CHARACTER VARYING;

ALTER TABLE nw.payout
  ADD COLUMN type_account_international_bank_number CHARACTER VARYING;
ALTER TABLE nw.payout
  ADD COLUMN type_account_international_bank_aba_rtn CHARACTER VARYING;
ALTER TABLE nw.payout
  ADD COLUMN type_account_international_bank_country_code CHARACTER VARYING;

ALTER TABLE nw.payout
  ADD COLUMN type_account_international_correspondent_bank_number CHARACTER VARYING;
ALTER TABLE nw.payout
  ADD COLUMN type_account_international_correspondent_bank_account CHARACTER VARYING;
ALTER TABLE nw.payout
  ADD COLUMN type_account_international_correspondent_bank_name CHARACTER VARYING;
ALTER TABLE nw.payout
  ADD COLUMN type_account_international_correspondent_bank_address CHARACTER VARYING;
ALTER TABLE nw.payout
  ADD COLUMN type_account_international_correspondent_bank_bic CHARACTER VARYING;
ALTER TABLE nw.payout
  ADD COLUMN type_account_international_correspondent_bank_iban CHARACTER VARYING;
ALTER TABLE nw.payout
  ADD COLUMN type_account_international_correspondent_bank_aba_rtn CHARACTER VARYING;
ALTER TABLE nw.payout
  ADD COLUMN type_account_international_correspondent_bank_country_code CHARACTER VARYING;