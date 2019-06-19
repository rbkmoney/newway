
select (refund.event_created_at at time zone 'UTC') at time zone 'MSK' as "DataMSK refunded",
    (refund.created_at at time zone 'UTC') at time zone 'MSK' as "DataMSK ref_created",
        refund.invoice_id || '.' || refund.payment_id as "SKO",
        refund.party_id as "Party_id",
        refund.shop_id as "Party_Shop_id",
        party.contact_info_email as "Party_Email",
      coalesce(payment.payer_digital_wallet_provider, payment.payer_bank_card_payment_system, payment.payer_payment_terminal_type) as "MPS",
        CASE
      WHEN payment.route_provider_id = 100 THEN 'Tinkoff'
      WHEN payment.route_provider_id = 101 THEN 'VTB / Assist MPI'
      WHEN payment.route_provider_id = 102 THEN 'Euroset'
      WHEN payment.route_provider_id = 103 THEN 'SNGB'
      WHEN payment.route_provider_id = 104 THEN 'VTB / Direct MPI'
      WHEN payment.route_provider_id = 105 THEN 'VTB / NKO / DPL / Direct MPI / Coolpay'
      WHEN payment.route_provider_id = 106 THEN 'QIWI'
      WHEN payment.route_provider_id = 107 THEN 'VTB / NKO / DPL / Direct MPI / Kassa'
      WHEN payment.route_provider_id = 108 THEN 'DPL / Rietumu Bank'
      WHEN payment.route_provider_id = 109 THEN 'VTB / NKO / DPL / Direct MPI / Certus'
      WHEN payment.route_provider_id = 110 THEN 'BRS'
      WHEN payment.route_provider_id = 111 THEN 'BRS / NKO / DPL / Direct MPI / Kassa'
      when payment.route_provider_id = 112 then 'BRS / AFT'
      when payment.route_provider_id = 113 then 'PSB'
      when payment.route_provider_id = 114 then 'Paycenter'
    END AS "Payment Provider",
    case
          when payment.payer_bank_card_payment_system is not null
              then payment.payer_bank_card_bin || '**' || payment.payer_bank_card_masked_pan
            when payment.payer_digital_wallet_provider is not null
              then payment.payer_digital_wallet_id
            when payment.payer_payment_terminal_type is not null
              then 'euroset'
        end as "Card mask",
        to_char(CAST(refund.amount as float)/100, '999 999 999.99') as "Refund Sum",
        to_char(CAST(refund.fee as float)/100, '999 999 999.99') as "Refund Comission",
        to_char(CAST(refund.provider_fee as float)/100, '999 999 999.99') as "Providers Refund Comission",
        to_char(CAST(refund.external_fee as float)/100, '999 999 999.99') as "Third Parties Refund Comission",
        refund.currency_code as "Currency",
        shop.details_name as "Merchant",
        shop.location_url as "URL",
        regexp_replace(invoice.details_product, E'[\\n\\r]|;+', ' ', 'g') as "Payment Details",
      --regexp_replace(rf.refund_reason, E'[\\n\\r;|]+', ' ', 'g') as "Refund Reason",
        payment.payer_ip_address as "Payment_IP",
        payment.payer_email as "Payment_Email",
    --es.invoice_description,
    case
        when refund.status = 'succeeded' then 'refunded'
    end as "Status vozvrata",
    contractor.russian_legal_entity_inn as "INN"
from nw.refund
join nw.payment
on (
  refund.invoice_id = payment.invoice_id
  and refund.payment_id = payment.payment_id
  and refund.event_created_at between ((('2019-03-04T00:00:00'::timestamp) AT TIME ZONE 'MSK') AT TIME ZONE 'UTC') AND ((('2019-03-05T00:00:00'::timestamp) at time zone 'MSK') at time zone 'UTC')
  and refund.status in ('succeeded')
  and refund.current
  and payment.current
)
join nw.invoice
on (
  refund.invoice_id = invoice.invoice_id
  and invoice.current
  )
join nw.shop
on (
  shop.party_id = refund.party_id
  and shop.shop_id = refund.shop_id
  and shop.current
)
join nw.party
on (
    shop.category_id not IN (1,2)
    and refund.party_id = party.party_id
    and party.current
)
join nw.contract
on (
    shop.party_id = contract.party_id AND
	shop.contract_id = contract.contract_id AND
    contract.current

)
join nw.contractor
on (
    contract.party_id = contractor.party_id AND
	contract.contractor_id = contractor.contractor_id AND
    contractor.current
)
ORDER BY refund.event_created_at asc;