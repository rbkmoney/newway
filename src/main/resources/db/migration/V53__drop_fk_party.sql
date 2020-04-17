alter table nw.contract_adjustment drop constraint if exists fk_adjustment_to_contract;
alter table nw.payout_tool drop constraint if exists fk_payout_tool_to_contract;