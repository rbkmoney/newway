DROP TABLE nw.payout cascade;
DROP TABLE nw.payout_summary cascade;

create table if not exists nw.payout
(
    id                bigserial            not null,
    payout_id         varchar              not null,
    event_created_at  timestamp            not null,
    sequence_id       int                  not null,
    created_at        timestamp            not null,
    party_id          varchar              not null,
    shop_id           varchar              not null,
    status            nw.payout_status     not null,
    payout_tool_id    varchar              not null,
    amount            bigint               not null,
    fee               bigint default 0     not null,
    currency_code     varchar              not null,
    cancelled_details varchar,
    wtime             TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
    current           BOOLEAN NOT NULL DEFAULT TRUE,
    constraint payout_id_pkey primary key (id),
    constraint payout_payout_id_ukey unique (payout_id, sequence_id)
    );