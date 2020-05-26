ALTER TABLE nw.adjustment
    ADD amount BIGINT;

ALTER TABLE nw.adjustment
    ADD guarantee_deposit BIGINT;

CREATE FUNCTION nw.get_adjustment_guarantee_deposit_sfunc(amounts BIGINT[], cash_flow nw.cash_flow)
    RETURNS BIGINT[]
    LANGUAGE plpgsql
    IMMUTABLE
    PARALLEL SAFE
AS
$$
BEGIN
    RETURN $1 || (
        nw.get_cashflow_sum(
                $2,
                'adjustment'::nw.payment_change_type,
                'merchant'::nw.cash_flow_account,
                '{"settlement"}',
                'merchant'::nw.cash_flow_account,
                '{"guarantee"}'
            )
        );
END;
$$;

CREATE AGGREGATE nw.get_adjustment_guarantee_deposit(nw.cash_flow)
    (
    SFUNC = nw.get_adjustment_guarantee_deposit_sfunc,
    STYPE = BIGINT[],
    PARALLEL = SAFE,
    FINALFUNC = cashflow_sum_finalfunc
    );
