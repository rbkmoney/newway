create function nw.get_cashflow_sum(_cash_flow nw.cash_flow, obj_type nw.payment_change_type, source_account_type nw.cash_flow_account, source_account_type_values varchar[], destination_account_type nw.cash_flow_account, destination_account_type_values varchar[])
returns bigint
language plpgsql
as $$
begin
  return (
    coalesce(
      (
        select sum(amount) from (select ($1).*) as cash_flow
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

create function nw.get_payment_amount(cash_flow nw.cash_flow)
returns bigint
language plpgsql
as $$
begin
  return (
    nw.get_cashflow_sum(
      $1,
      'payment'::nw.payment_change_type,
      'provider'::nw.cash_flow_account,
      '{"settlement"}',
      'merchant'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

create function nw.get_payment_fee(cash_flow nw.cash_flow)
returns bigint
language plpgsql
as $$
begin
  return (
    nw.get_cashflow_sum(
      $1,
      'payment'::nw.payment_change_type,
      'merchant'::nw.cash_flow_account,
      '{"settlement"}',
      'system'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

create function nw.get_payment_external_fee(cash_flow nw.cash_flow)
returns bigint
language plpgsql
as $$
begin
  return (
    nw.get_cashflow_sum(
      $1,
      'payment'::nw.payment_change_type,
      'system'::nw.cash_flow_account,
      '{"settlement"}',
      'external'::nw.cash_flow_account,
      '{"income", "outcome"}'
    )
  );
end;
$$;

create function nw.get_payment_provider_fee(cash_flow nw.cash_flow)
returns bigint
language plpgsql
as $$
begin
  return (
    nw.get_cashflow_sum(
      $1,
      'payment'::nw.payment_change_type,
      'system'::nw.cash_flow_account,
      '{"settlement"}',
      'provider'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

create function nw.get_payment_guarantee_deposit(cash_flow nw.cash_flow)
returns bigint
language plpgsql
as $$
begin
  return (
    nw.get_cashflow_sum(
      $1,
      'payment'::nw.payment_change_type,
      'merchant'::nw.cash_flow_account,
      '{"settlement"}',
      'merchant'::nw.cash_flow_account,
      '{"guarantee"}'
    )
  );
end;
$$;

create function nw.get_refund_amount(cash_flow nw.cash_flow)
returns bigint
language plpgsql
as $$
begin
  return (
    nw.get_cashflow_sum(
      $1,
      'refund'::nw.payment_change_type,
      'merchant'::nw.cash_flow_account,
      '{"settlement"}',
      'provider'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

create function nw.get_refund_fee(cash_flow nw.cash_flow)
returns bigint
language plpgsql
as $$
begin
  return (
    nw.get_cashflow_sum(
      $1,
      'refund'::nw.payment_change_type,
      'merchant'::nw.cash_flow_account,
      '{"settlement"}',
      'system'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

create function nw.get_refund_external_fee(cash_flow nw.cash_flow)
returns bigint
language plpgsql
as $$
begin
  return (
    nw.get_cashflow_sum(
      $1,
      'refund'::nw.payment_change_type,
      'system'::nw.cash_flow_account,
      '{"settlement"}',
      'external'::nw.cash_flow_account,
      '{"income", "outcome"}'
    )
  );
end;
$$;

create function nw.get_refund_provider_fee(cash_flow nw.cash_flow)
returns bigint
language plpgsql
as $$
begin
  return (
    nw.get_cashflow_sum(
      $1,
      'refund'::nw.payment_change_type,
      'system'::nw.cash_flow_account,
      '{"settlement"}',
      'provider'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

create function nw.get_payout_amount(cash_flow nw.cash_flow)
returns bigint
language plpgsql
as $$
begin
  return (
    nw.get_cashflow_sum(
      $1,
      'payout'::nw.payment_change_type,
      'merchant'::nw.cash_flow_account,
      '{"settlement"}',
      'merchant'::nw.cash_flow_account,
      '{"payout"}'
    )
  );
end;
$$;

create function nw.get_payout_fee(cash_flow nw.cash_flow)
returns bigint
language plpgsql
as $$
begin
  return (
    nw.get_cashflow_sum(
      $1,
      'payout'::nw.payment_change_type,
      'merchant'::nw.cash_flow_account,
      '{"settlement"}',
      'system'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

create function nw.get_payout_fixed_fee(cash_flow nw.cash_flow)
returns bigint
language plpgsql
as $$
begin
  return (
    nw.get_cashflow_sum(
      $1,
      'payout'::nw.payment_change_type,
      'merchant'::nw.cash_flow_account,
      '{"payout"}',
      'system'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

create function nw.get_adjustment_amount(cash_flow nw.cash_flow)
returns bigint
language plpgsql
as $$
begin
  return (
    nw.get_cashflow_sum(
      $1,
      'adjustment'::nw.payment_change_type,
      'provider'::nw.cash_flow_account,
      '{"settlement"}',
      'merchant'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

create function nw.get_adjustment_fee(cash_flow nw.cash_flow)
returns bigint
language plpgsql
as $$
begin
  return (
    nw.get_cashflow_sum(
      $1,
      'adjustment'::nw.payment_change_type,
      'merchant'::nw.cash_flow_account,
      '{"settlement"}',
      'system'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

create function nw.get_adjustment_external_fee(cash_flow nw.cash_flow)
returns bigint
language plpgsql
as $$
begin
  return (
    nw.get_cashflow_sum(
      $1,
      'adjustment'::nw.payment_change_type,
      'system'::nw.cash_flow_account,
      '{"settlement"}',
      'external'::nw.cash_flow_account,
      '{"income", "outcome"}'
    )
  );
end;
$$;

create function nw.get_adjustment_provider_fee(cash_flow nw.cash_flow)
returns bigint
language plpgsql
as $$
begin
  return (
    nw.get_cashflow_sum(
      $1,
      'adjustment'::nw.payment_change_type,
      'system'::nw.cash_flow_account,
      '{"settlement"}',
      'provider'::nw.cash_flow_account,
      '{"settlement"}'
    )
  );
end;
$$;

