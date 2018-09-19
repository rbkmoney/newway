create or replace function nw.get_cashflow_sum(_cash_flow nw.cash_flow, obj_type nw.payment_change_type, source_account_type nw.cash_flow_account, source_account_type_values varchar[], destination_account_type nw.cash_flow_account, destination_account_type_values varchar[])
returns bigint
language plpgsql
immutable
parallel safe
as $$
begin
  return (
    coalesce(
      (
        select amount from (select ($1).*) as cash_flow
          where cash_flow.obj_type = $2
          and cash_flow.source_account_type = $3
          and cash_flow.source_account_type_value = ANY ($4)
          and cash_flow.destination_account_type = $5
          and cash_flow.destination_account_type_value = ANY ($6)
          and (
            (cash_flow.obj_type = 'adjustment' and cash_flow.adj_flow_type = 'new_cash_flow')
            or (cash_flow.obj_type != 'adjustment' and cash_flow.adj_flow_type is null)
          )
        ), 0)
  );
end;
$$;

create or replace function nw.cashflow_sum_finalfunc(amount bigint)
returns bigint
language plpgsql
immutable
parallel safe
as $$
begin
  return amount;
end;
$$;

create or replace function nw.get_payment_amount_sfunc(amount bigint, cash_flow nw.cash_flow)
returns bigint
language plpgsql
immutable
parallel safe
as $$
begin
  return $1 + (
    nw.get_cashflow_sum(
      $2,
      'payment'::nw.payment_change_type,
      'provider'::nw.cash_flow_account,
      '{"settlement"}',
      'merchant'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

drop aggregate nw.get_payment_amount(nw.cash_flow);
create aggregate nw.get_payment_amount(nw.cash_flow)
(
  sfunc     = nw.get_payment_amount_sfunc,
  stype     = bigint,
  finalfunc = cashflow_sum_finalfunc,
  parallel  = safe,
  initcond  = 0
);

create or replace function nw.get_payment_fee_sfunc(amount bigint, cash_flow nw.cash_flow)
returns bigint
language plpgsql
immutable
parallel safe
as $$
begin
  return $1 + (
    nw.get_cashflow_sum(
      $2,
      'payment'::nw.payment_change_type,
      'merchant'::nw.cash_flow_account,
      '{"settlement"}',
      'system'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

drop aggregate nw.get_payment_fee(nw.cash_flow);
create aggregate nw.get_payment_fee(nw.cash_flow)
(
  sfunc     = nw.get_payment_fee_sfunc,
  stype     = bigint,
  finalfunc = cashflow_sum_finalfunc,
  parallel  = safe,
  initcond  = 0
);

create or replace function nw.get_payment_external_fee_sfunc(amount bigint, cash_flow nw.cash_flow)
returns bigint
language plpgsql
immutable
parallel safe
as $$
begin
  return $1 + (
    nw.get_cashflow_sum(
      $2,
      'payment'::nw.payment_change_type,
      'system'::nw.cash_flow_account,
      '{"settlement"}',
      'external'::nw.cash_flow_account,
      '{"income", "outcome"}'
    )
  );
end;
$$;

drop aggregate nw.get_payment_external_fee(nw.cash_flow);
create aggregate nw.get_payment_external_fee(nw.cash_flow)
(
  sfunc     = nw.get_payment_external_fee_sfunc,
  stype     = bigint,
  finalfunc = cashflow_sum_finalfunc,
  parallel  = safe,
  initcond  = 0
);

create or replace function nw.get_payment_provider_fee_sfunc(amount bigint, cash_flow nw.cash_flow)
returns bigint
language plpgsql
immutable
parallel safe
as $$
begin
  return $1 + (
    nw.get_cashflow_sum(
      $2,
      'payment'::nw.payment_change_type,
      'system'::nw.cash_flow_account,
      '{"settlement"}',
      'provider'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

drop aggregate nw.get_payment_provider_fee(nw.cash_flow);
create aggregate nw.get_payment_provider_fee(nw.cash_flow)
(
  sfunc     = nw.get_payment_provider_fee_sfunc,
  stype     = bigint,
  finalfunc = cashflow_sum_finalfunc,
  parallel  = safe,
  initcond  = 0
);

create or replace function nw.get_payment_guarantee_deposit_sfunc(amount bigint, cash_flow nw.cash_flow)
returns bigint
language plpgsql
immutable
parallel safe
as $$
begin
  return $1 + (
    nw.get_cashflow_sum(
      $2,
      'payment'::nw.payment_change_type,
      'merchant'::nw.cash_flow_account,
      '{"settlement"}',
      'merchant'::nw.cash_flow_account,
      '{"guarantee"}'
    )
  );
end;
$$;

drop aggregate nw.get_payment_guarantee_deposit(nw.cash_flow);
create aggregate nw.get_payment_guarantee_deposit(nw.cash_flow)
(
  sfunc     = nw.get_payment_guarantee_deposit_sfunc,
  stype     = bigint,
  finalfunc = cashflow_sum_finalfunc,
  parallel  = safe,
  initcond  = 0
);

create or replace function nw.get_refund_amount_sfunc(amount bigint, cash_flow nw.cash_flow)
returns bigint
language plpgsql
immutable
parallel safe
as $$
begin
  return $1 + (
    nw.get_cashflow_sum(
      $2,
      'refund'::nw.payment_change_type,
      'merchant'::nw.cash_flow_account,
      '{"settlement"}',
      'provider'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

drop aggregate nw.get_refund_amount(nw.cash_flow);
create aggregate nw.get_refund_amount(nw.cash_flow)
(
  sfunc     = nw.get_refund_amount_sfunc,
  stype     = bigint,
  finalfunc = cashflow_sum_finalfunc,
  parallel  = safe,
  initcond  = 0
);

create or replace function nw.get_refund_fee_sfunc(amount bigint, cash_flow nw.cash_flow)
returns bigint
language plpgsql
immutable
parallel safe
as $$
begin
  return $1 + (
    nw.get_cashflow_sum(
      $2,
      'refund'::nw.payment_change_type,
      'merchant'::nw.cash_flow_account,
      '{"settlement"}',
      'system'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

drop aggregate nw.get_refund_fee(nw.cash_flow);
create aggregate nw.get_refund_fee(nw.cash_flow)
(
  sfunc     = nw.get_refund_fee_sfunc,
  stype     = bigint,
  finalfunc = cashflow_sum_finalfunc,
  parallel  = safe,
  initcond  = 0
);

create or replace function nw.get_refund_external_fee_sfunc(amount bigint, cash_flow nw.cash_flow)
returns bigint
language plpgsql
immutable
parallel safe
as $$
begin
  return $1 + (
    nw.get_cashflow_sum(
      $2,
      'refund'::nw.payment_change_type,
      'system'::nw.cash_flow_account,
      '{"settlement"}',
      'external'::nw.cash_flow_account,
      '{"income", "outcome"}'
    )
  );
end;
$$;

drop aggregate nw.get_refund_external_fee(nw.cash_flow);
create aggregate nw.get_refund_external_fee(nw.cash_flow)
(
  sfunc     = nw.get_refund_external_fee_sfunc,
  stype     = bigint,
  finalfunc = cashflow_sum_finalfunc,
  parallel  = safe,
  initcond  = 0
);

create or replace function nw.get_refund_provider_fee_sfunc(amount bigint, cash_flow nw.cash_flow)
returns bigint
language plpgsql
immutable
parallel safe
as $$
begin
  return $1 + (
    nw.get_cashflow_sum(
      $2,
      'refund'::nw.payment_change_type,
      'system'::nw.cash_flow_account,
      '{"settlement"}',
      'provider'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

drop aggregate nw.get_refund_provider_fee(nw.cash_flow);
create aggregate nw.get_refund_provider_fee(nw.cash_flow)
(
  sfunc     = nw.get_refund_provider_fee_sfunc,
  stype     = bigint,
  finalfunc = cashflow_sum_finalfunc,
  parallel  = safe,
  initcond  = 0
);

create or replace function nw.get_payout_amount_sfunc(amount bigint, cash_flow nw.cash_flow)
returns bigint
language plpgsql
immutable
parallel safe
as $$
begin
  return $1 + (
    nw.get_cashflow_sum(
      $2,
      'payout'::nw.payment_change_type,
      'merchant'::nw.cash_flow_account,
      '{"settlement"}',
      'merchant'::nw.cash_flow_account,
      '{"payout"}'
    )
  );
end;
$$;

drop aggregate nw.get_payout_amount(nw.cash_flow);
create aggregate nw.get_payout_amount(nw.cash_flow)
(
  sfunc     = nw.get_payout_amount_sfunc,
  stype     = bigint,
  finalfunc = cashflow_sum_finalfunc,
  parallel  = safe,
  initcond  = 0
);

create or replace function nw.get_payout_fee_sfunc(amount bigint, cash_flow nw.cash_flow)
returns bigint
language plpgsql
immutable
parallel safe
as $$
begin
  return $1 + (
    nw.get_cashflow_sum(
      $2,
      'payout'::nw.payment_change_type,
      'merchant'::nw.cash_flow_account,
      '{"settlement"}',
      'system'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

drop aggregate nw.get_payout_fee(nw.cash_flow);
create aggregate nw.get_payout_fee(nw.cash_flow)
(
  sfunc     = nw.get_payout_fee_sfunc,
  stype     = bigint,
  finalfunc = cashflow_sum_finalfunc,
  parallel  = safe,
  initcond  = 0
);


create or replace function nw.get_payout_fixed_fee_sfunc(amount bigint, cash_flow nw.cash_flow)
returns bigint
language plpgsql
immutable
parallel safe
as $$
begin
  return $1 + (
    nw.get_cashflow_sum(
      $2,
      'payout'::nw.payment_change_type,
      'merchant'::nw.cash_flow_account,
      '{"payout"}',
      'system'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

drop aggregate nw.get_payout_fixed_fee(nw.cash_flow);
create aggregate nw.get_payout_fixed_fee(nw.cash_flow)
(
  sfunc     = nw.get_payout_fixed_fee_sfunc,
  stype     = bigint,
  finalfunc = cashflow_sum_finalfunc,
  parallel  = safe,
  initcond  = 0
);

create or replace function nw.get_adjustment_amount_sfunc(amount bigint, cash_flow nw.cash_flow)
returns bigint
language plpgsql
immutable
parallel safe
as $$
begin
  return $1 + (
    nw.get_cashflow_sum(
      $2,
      'adjustment'::nw.payment_change_type,
      'provider'::nw.cash_flow_account,
      '{"settlement"}',
      'merchant'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

drop aggregate nw.get_adjustment_amount(nw.cash_flow);
create aggregate nw.get_adjustment_amount(nw.cash_flow)
(
  sfunc     = nw.get_adjustment_amount_sfunc,
  stype     = bigint,
  finalfunc = cashflow_sum_finalfunc,
  parallel  = safe,
  initcond  = 0
);

create or replace function nw.get_adjustment_fee_sfunc(amount bigint, cash_flow nw.cash_flow)
returns bigint
language plpgsql
immutable
parallel safe
as $$
begin
  return $1 + (
    nw.get_cashflow_sum(
      $2,
      'adjustment'::nw.payment_change_type,
      'merchant'::nw.cash_flow_account,
      '{"settlement"}',
      'system'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

drop aggregate nw.get_adjustment_fee(nw.cash_flow);
create aggregate nw.get_adjustment_fee(nw.cash_flow)
(
  sfunc     = nw.get_adjustment_fee_sfunc,
  stype     = bigint,
  finalfunc = cashflow_sum_finalfunc,
  parallel  = safe,
  initcond  = 0
);

create or replace function nw.get_adjustment_external_fee_sfunc(amount bigint, cash_flow nw.cash_flow)
returns bigint
language plpgsql
immutable
parallel safe
as $$
begin
  return $1 + (
    nw.get_cashflow_sum(
      $2,
      'adjustment'::nw.payment_change_type,
      'system'::nw.cash_flow_account,
      '{"settlement"}',
      'external'::nw.cash_flow_account,
      '{"income", "outcome"}'
    )
  );
end;
$$;

drop aggregate nw.get_adjustment_external_fee(nw.cash_flow);
create aggregate nw.get_adjustment_external_fee(nw.cash_flow)
(
  sfunc     = nw.get_adjustment_external_fee_sfunc,
  stype     = bigint,
  finalfunc = cashflow_sum_finalfunc,
  parallel  = safe,
  initcond  = 0
);

create or replace function nw.get_adjustment_provider_fee_sfunc(amount bigint, cash_flow nw.cash_flow)
returns bigint
language plpgsql
immutable
parallel safe
as $$
begin
  return $1 + (
    nw.get_cashflow_sum(
      $2,
      'adjustment'::nw.payment_change_type,
      'system'::nw.cash_flow_account,
      '{"settlement"}',
      'provider'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

drop aggregate nw.get_adjustment_provider_fee(nw.cash_flow);
create aggregate nw.get_adjustment_provider_fee(nw.cash_flow)
(
  sfunc     = nw.get_adjustment_provider_fee_sfunc,
  stype     = bigint,
  finalfunc = cashflow_sum_finalfunc,
  parallel  = safe,
  initcond  = 0
);

