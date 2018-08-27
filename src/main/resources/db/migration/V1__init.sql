CREATE SCHEMA IF NOT EXISTS nw;

-- invoices --

CREATE TYPE nw.InvoiceStatus AS ENUM('unpaid', 'paid', 'cancelled', 'fulfilled');

CREATE TABLE nw.invoice(
  id                       BIGSERIAL NOT NULL,
  event_id                 BIGINT NOT NULL,
  event_created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  invoice_id               CHARACTER VARYING NOT NULL,
  party_id                 CHARACTER VARYING NOT NULL,
  shop_id                  CHARACTER VARYING NOT NULL,
  party_revision           BIGINT,
  created_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  status                   nw.InvoiceStatus NOT NULL,
  status_cancelled_details CHARACTER VARYING,
  status_fulfilled_details CHARACTER VARYING,
  details_product          CHARACTER VARYING NOT NULL,
  details_description      CHARACTER VARYING,
  due                      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  amount                   BIGINT NOT NULL,
  currency_code            CHARACTER VARYING NOT NULL,
  context                  BYTEA,
  template_id              CHARACTER VARYING,
  wtime                    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  current                  BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT invoice_pkey PRIMARY KEY (id)
);

CREATE INDEX invoice_event_id on nw.invoice(event_id);
CREATE INDEX invoice_event_created_at on nw.invoice(event_created_at);
CREATE INDEX invoice_invoice_id on nw.invoice(invoice_id);
CREATE INDEX invoice_party_id on nw.invoice(party_id);
CREATE INDEX invoice_status on nw.invoice(status);
CREATE INDEX invoice_created_at on nw.invoice(created_at);

CREATE TABLE nw.invoice_cart (
  id            BIGSERIAL NOT NULL,
  inv_id        BIGINT NOT NULL,
  product       CHARACTER VARYING NOT NULL,
  quantity      INT NOT NULL,
  amount        BIGINT NOT NULL,
  currency_code CHARACTER VARYING NOT NULL,
  metadata_json CHARACTER VARYING NOT NULL,
  CONSTRAINT invoice_cart_pkey PRIMARY KEY (id),
  CONSTRAINT fk_cart_to_invoice FOREIGN KEY (inv_id) REFERENCES nw.invoice(id)
);

CREATE INDEX invoice_cart_inv_id on nw.invoice_cart(inv_id);

-- payments --

CREATE TYPE nw.PaymentStatus AS ENUM ('pending', 'processed', 'captured', 'cancelled', 'refunded', 'failed');
CREATE TYPE nw.PayerType AS ENUM('payment_resource', 'customer');
CREATE TYPE nw.PaymentToolType AS ENUM('bank_card', 'payment_terminal', 'digital_wallet');
CREATE TYPE nw.PaymentFlowType AS ENUM('instant', 'hold');
CREATE TYPE nw.RiskScore AS ENUM('low', 'high', 'fatal');

CREATE TABLE nw.payment (
  id                                 BIGSERIAL                   NOT NULL,
  event_id                           BIGINT                      NOT NULL,
  event_created_at                   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  payment_id                         CHARACTER VARYING           NOT NULL,
  created_at                         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  invoice_id                         CHARACTER VARYING           NOT NULL,
  party_id                           CHARACTER VARYING           NOT NULL,
  shop_id                            CHARACTER VARYING           NOT NULL,
  domain_revision                    BIGINT                      NOT NULL,
  party_revision                     BIGINT,
  status                             nw.PaymentStatus            NOT NULL,
  status_cancelled_reason            CHARACTER VARYING,
  status_captured_reason             CHARACTER VARYING,
  status_failed_failure              CHARACTER VARYING,
  amount                             BIGINT                      NOT NULL,
  currency_code                      CHARACTER VARYING           NOT NULL,
  payer_type                         nw.PayerType                NOT NULL,
  payer_payment_tool_type            nw.PaymentToolType          NOT NULL,
  payer_bank_card_token              CHARACTER VARYING,
  payer_bank_card_payment_system     CHARACTER VARYING,
  payer_bank_card_bin                CHARACTER VARYING,
  payer_bank_card_masked_pan         CHARACTER VARYING,
  payer_bank_card_token_provider     CHARACTER VARYING,
  payer_payment_terminal_type        CHARACTER VARYING,
  payer_digital_wallet_provider      CHARACTER VARYING,
  payer_digital_wallet_id            CHARACTER VARYING,
  payer_payment_session_id           CHARACTER VARYING,
  payer_ip_address                   CHARACTER VARYING,
  payer_fingerprint                  CHARACTER VARYING,
  payer_phone_number                 CHARACTER VARYING,
  payer_email                        CHARACTER VARYING,
  payer_customer_id                  CHARACTER VARYING,
  payer_customer_binding_id          CHARACTER VARYING,
  payer_customer_rec_payment_tool_id CHARACTER VARYING,
  context                            BYTEA,
  payment_flow_type                  nw.PaymentFlowType          NOT NULL,
  payment_flow_on_hold_expiration    CHARACTER VARYING,
  payment_flow_held_until            TIMESTAMP WITHOUT TIME ZONE,
  risk_score                         nw.RiskScore,
  route_provider_id                  INT,
  route_terminal_id                  INT,
  wtime                              TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  current                            BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT payment_pkey PRIMARY KEY (id)
);

CREATE INDEX payment_event_id on nw.payment(event_id);
CREATE INDEX payment_event_created_at on nw.payment(event_created_at);
CREATE INDEX payment_invoice_id on nw.payment(invoice_id);
CREATE INDEX payment_party_id on nw.payment(party_id);
CREATE INDEX payment_status on nw.payment(status);
CREATE INDEX payment_created_at on nw.payment(created_at);

CREATE TYPE nw.CashFlowAccount AS ENUM ('merchant', 'provider', 'system', 'external', 'wallet');

CREATE TYPE nw.PaymentChangeType AS ENUM ('payment', 'refund', 'adjustment', 'payout');

CREATE TYPE nw.AdjustmentCashFlowType AS ENUM ('new_cash_flow', 'old_cash_flow_inverse');

CREATE TABLE nw.cash_flow(
  id                                 BIGSERIAL                   NOT NULL,
  obj_id                             BIGINT                      NOT NULL,
  obj_type                           nw.PaymentChangeType        NOT NULL,
  adj_flow_type                      nw.AdjustmentCashFlowType,
  source_account_type                nw.CashFlowAccount          NOT NULL,
  source_account_type_value          CHARACTER VARYING           NOT NULL,
  source_account_id                  BIGINT                      NOT NULL,
  destination_account_type           nw.CashFlowAccount          NOT NULL,
  destination_account_type_value     CHARACTER VARYING           NOT NULL,
  destination_account_id             BIGINT                      NOT NULL,
  amount                             BIGINT                      NOT NULL,
  currency_code                      CHARACTER VARYING           NOT NULL,
  details                            CHARACTER VARYING,
  CONSTRAINT cash_flow_pkey PRIMARY KEY (id)
);

CREATE INDEX cash_flow_idx on nw.cash_flow(obj_id, obj_type);

-- refunds --

CREATE TYPE nw.RefundStatus AS ENUM ('pending', 'succeeded', 'failed');

CREATE TABLE nw.refund (
  id                                 BIGSERIAL                   NOT NULL,
  event_id                           BIGINT                      NOT NULL,
  event_created_at                   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  domain_revision                    BIGINT                      NOT NULL,
  refund_id                          CHARACTER VARYING           NOT NULL,
  payment_id                         CHARACTER VARYING           NOT NULL,
  invoice_id                         CHARACTER VARYING           NOT NULL,
  party_id                           CHARACTER VARYING           NOT NULL,
  shop_id                            CHARACTER VARYING           NOT NULL,
  created_at                         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  status                             nw.RefundStatus             NOT NULL,
  status_failed_failure              CHARACTER VARYING,
  amount                             BIGINT,
  currency_code                      CHARACTER VARYING,
  reason                             CHARACTER VARYING,
  wtime                              TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  current                            BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT refund_pkey PRIMARY KEY (id)
);

CREATE INDEX refund_event_id on nw.refund(event_id);
CREATE INDEX refund_event_created_at on nw.refund(event_created_at);
CREATE INDEX refund_invoice_id on nw.refund(invoice_id);
CREATE INDEX refund_party_id on nw.refund(party_id);
CREATE INDEX refund_status on nw.refund(status);
CREATE INDEX refund_created_at on nw.refund(created_at);

-- adjustments --

CREATE TYPE nw.AdjustmentStatus AS ENUM ('pending', 'captured', 'cancelled');

CREATE TABLE nw.adjustment (
  id                                 BIGSERIAL                   NOT NULL,
  event_id                           BIGINT                      NOT NULL,
  event_created_at                   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  domain_revision                    BIGINT                      NOT NULL,
  adjustment_id                      CHARACTER VARYING           NOT NULL,
  payment_id                         CHARACTER VARYING           NOT NULL,
  invoice_id                         CHARACTER VARYING           NOT NULL,
  party_id                           CHARACTER VARYING           NOT NULL,
  shop_id                            CHARACTER VARYING           NOT NULL,
  created_at                         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  status                             nw.AdjustmentStatus         NOT NULL,
  status_captured_at                 TIMESTAMP WITHOUT TIME ZONE,
  status_cancelled_at                TIMESTAMP WITHOUT TIME ZONE,
  reason                             CHARACTER VARYING           NOT NULL,
  wtime                              TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  current                            BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT adjustment_pkey PRIMARY KEY (id)
);

CREATE INDEX adjustment_event_id on nw.adjustment(event_id);
CREATE INDEX adjustment_event_created_at on nw.adjustment(event_created_at);
CREATE INDEX adjustment_invoice_id on nw.adjustment(invoice_id);
CREATE INDEX adjustment_party_id on nw.adjustment(party_id);
CREATE INDEX adjustment_status on nw.adjustment(status);
CREATE INDEX adjustment_created_at on nw.adjustment(created_at);

-----------
-- party_mngmnt --
-----------

CREATE TYPE nw.Blocking AS ENUM ('unblocked', 'blocked');
CREATE TYPE nw.Suspension AS ENUM ('active', 'suspended');

CREATE TABLE nw.party(
  id                                 BIGSERIAL                   NOT NULL,
  event_id                           BIGINT                      NOT NULL,
  event_created_at                   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  party_id                           CHARACTER VARYING           NOT NULL,
  contact_info_email                 CHARACTER VARYING           NOT NULL,
  created_at                         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  blocking                           nw.Blocking                 NOT NULL,
  blocking_unblocked_reason          CHARACTER VARYING,
  blocking_unblocked_since           TIMESTAMP WITHOUT TIME ZONE,
  blocking_blocked_reason            CHARACTER VARYING,
  blocking_blocked_since             TIMESTAMP WITHOUT TIME ZONE,
  suspension                         nw.Suspension               NOT NULL,
  suspension_active_since            TIMESTAMP WITHOUT TIME ZONE,
  suspension_suspended_since         TIMESTAMP WITHOUT TIME ZONE,
  revision                           BIGINT                      NOT NULL,
  revision_changed_at                TIMESTAMP WITHOUT TIME ZONE,
  party_meta_set_ns                  CHARACTER VARYING,
  party_meta_set_data_json           CHARACTER VARYING,
  wtime                              TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  current                            BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT party_pkey PRIMARY KEY (id)
);

CREATE INDEX party_event_id on nw.party(event_id);
CREATE INDEX party_event_created_at on nw.party(event_created_at);
CREATE INDEX party_party_id on nw.party(party_id);
CREATE INDEX party_current on nw.party(current);
CREATE INDEX party_created_at on nw.party(created_at);
CREATE INDEX party_contact_info_email on nw.party(contact_info_email);

-- contract --

CREATE TYPE nw.ContractStatus AS ENUM ('active', 'terminated', 'expired');
CREATE TYPE nw.RepresentativeDocument AS ENUM ('articles_of_association', 'power_of_attorney', 'expired');

CREATE TABLE nw.contract(
  id                                                         BIGSERIAL                   NOT NULL,
  event_id                                                   BIGINT                      NOT NULL,
  event_created_at                                           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  contract_id                                                CHARACTER VARYING           NOT NULL,
  party_id                                                   CHARACTER VARYING           NOT NULL,
  payment_institution_id                                     INT,
  created_at                                                 TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  valid_since                                                TIMESTAMP WITHOUT TIME ZONE,
  valid_until                                                TIMESTAMP WITHOUT TIME ZONE,
  status                                                     nw.ContractStatus           NOT NULL,
  status_terminated_at                                       TIMESTAMP WITHOUT TIME ZONE,
  terms_id                                                   INT                         NOT NULL,
  legal_agreement_signed_at                                  TIMESTAMP WITHOUT TIME ZONE,
  legal_agreement_id                                         CHARACTER VARYING,
  legal_agreement_valid_until                                TIMESTAMP WITHOUT TIME ZONE,
  report_act_schedule_id                                     INT,
  report_act_signer_position                                 CHARACTER VARYING,
  report_act_signer_full_name                                CHARACTER VARYING,
  report_act_signer_document                                 nw.RepresentativeDocument,
  report_act_signer_doc_power_of_attorney_signed_at          TIMESTAMP WITHOUT TIME ZONE,
  report_act_signer_doc_power_of_attorney_legal_agreement_id CHARACTER VARYING,
  report_act_signer_doc_power_of_attorney_valid_until        TIMESTAMP WITHOUT TIME ZONE,
  contractor_id                                              CHARACTER VARYING,
  wtime                                                      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  current                                                    BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT contract_pkey PRIMARY KEY (id)
);

CREATE INDEX contract_event_id on nw.contract(event_id);
CREATE INDEX contract_event_created_at on nw.contract(event_created_at);
CREATE INDEX contract_contract_id on nw.contract(contract_id);
CREATE INDEX contract_party_id on nw.contract(party_id);
CREATE INDEX contract_created_at on nw.contract(created_at);

CREATE TABLE nw.contract_adjustment(
  id                                 BIGSERIAL                   NOT NULL,
  cntrct_id                          BIGINT                      NOT NULL,
  contract_adjustment_id             CHARACTER VARYING           NOT NULL,
  created_at                         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  valid_since                        TIMESTAMP WITHOUT TIME ZONE,
  valid_until                        TIMESTAMP WITHOUT TIME ZONE,
  terms_id                           INT                         NOT NULL,
  CONSTRAINT contract_adjustment_pkey PRIMARY KEY (id),
  CONSTRAINT fk_adjustment_to_contract FOREIGN KEY (cntrct_id) REFERENCES nw.contract(id)
);

CREATE INDEX contract_adjustment_idx on nw.contract_adjustment(cntrct_id);

CREATE TYPE nw.PayoutToolInfo AS ENUM ('russian_bank_account', 'international_bank_account');

CREATE TABLE nw.payout_tool(
  id                                                 BIGSERIAL                   NOT NULL,
  cntrct_id                                          BIGINT                      NOT NULL,
  payout_tool_id                                     CHARACTER VARYING           NOT NULL,
  created_at                                         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  currency_code                                      CHARACTER VARYING           NOT NULL,
  payout_tool_info                                   nw.PayoutToolInfo           NOT NULL,
  payout_tool_info_russian_bank_account              CHARACTER VARYING,
  payout_tool_info_russian_bank_name                 CHARACTER VARYING,
  payout_tool_info_russian_bank_post_account         CHARACTER VARYING,
  payout_tool_info_russian_bank_bik                  CHARACTER VARYING,
  payout_tool_info_international_bank_account_holder CHARACTER VARYING,
  payout_tool_info_international_bank_name           CHARACTER VARYING,
  payout_tool_info_international_bank_address        CHARACTER VARYING,
  payout_tool_info_international_bank_iban           CHARACTER VARYING,
  payout_tool_info_international_bank_bic            CHARACTER VARYING,
  payout_tool_info_international_bank_local_code     CHARACTER VARYING,
  CONSTRAINT payout_tool_pkey PRIMARY KEY (id),
  CONSTRAINT fk_payout_tool_to_contract FOREIGN KEY (cntrct_id) REFERENCES nw.contract(id)
);

CREATE INDEX payout_tool_idx on nw.payout_tool(cntrct_id);

-- contractor --

CREATE TYPE nw.ContractorType AS ENUM ('registered_user', 'legal_entity', 'private_entity');
CREATE TYPE nw.LegalEntity AS ENUM ('russian_legal_entity', 'international_legal_entity');
CREATE TYPE nw.PrivateEntity AS ENUM ('russian_private_entity');

CREATE TABLE nw.contractor(
  id                                              BIGSERIAL                   NOT NULL,
  event_id                                        BIGINT                      NOT NULL,
  event_created_at                                TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  party_id                                        CHARACTER VARYING           NOT NULL,
  contractor_id                                   CHARACTER VARYING           NOT NULL,
  type                                            nw.ContractorType           NOT NULL,
  identificational_level                          CHARACTER VARYING,
  registered_user_email                           CHARACTER VARYING,
  legal_entity                                    nw.LegalEntity,
  russian_legal_entity_registered_name            CHARACTER VARYING,
  russian_legal_entity_registered_number          CHARACTER VARYING,
  russian_legal_entity_inn                        CHARACTER VARYING,
  russian_legal_entity_actual_address             CHARACTER VARYING,
  russian_legal_entity_post_address               CHARACTER VARYING,
  russian_legal_entity_representative_position    CHARACTER VARYING,
  russian_legal_entity_representative_full_name   CHARACTER VARYING,
  russian_legal_entity_representative_document    CHARACTER VARYING,
  russian_legal_entity_russian_bank_account       CHARACTER VARYING,
  russian_legal_entity_russian_bank_name          CHARACTER VARYING,
  russian_legal_entity_russian_bank_post_account  CHARACTER VARYING,
  russian_legal_entity_russian_bank_bik           CHARACTER VARYING,
  international_legal_entity_legal_name           CHARACTER VARYING,
  international_legal_entity_trading_name         CHARACTER VARYING,
  international_legal_entity_registered_address   CHARACTER VARYING,
  international_legal_entity_actual_address       CHARACTER VARYING,
  international_legal_entity_registered_number    CHARACTER VARYING,
  private_entity                                  nw.PrivateEntity,
  russian_private_entity_first_name               CHARACTER VARYING,
  russian_private_entity_second_name              CHARACTER VARYING,
  russian_private_entity_middle_name              CHARACTER VARYING,
  russian_private_entity_phone_number             CHARACTER VARYING,
  russian_private_entity_email                    CHARACTER VARYING,
  wtime                                           TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  current                                         BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT contractor_pkey PRIMARY KEY (id)
);

CREATE INDEX contractor_event_id on nw.contractor(event_id);
CREATE INDEX contractor_event_created_at on nw.contractor(event_created_at);
CREATE INDEX contractor_contractor_id on nw.contractor(contractor_id);
CREATE INDEX contractor_party_id on nw.contractor(party_id);

-- shop --

CREATE TABLE nw.shop(
  id                                              BIGSERIAL                   NOT NULL,
  event_id                                        BIGINT                      NOT NULL,
  event_created_at                                TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  party_id                                        CHARACTER VARYING           NOT NULL,
  shop_id                                         CHARACTER VARYING           NOT NULL,
  created_at                                      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  blocking                                        nw.Blocking                 NOT NULL,
  blocking_unblocked_reason                       CHARACTER VARYING,
  blocking_unblocked_since                        TIMESTAMP WITHOUT TIME ZONE,
  blocking_blocked_reason                         CHARACTER VARYING,
  blocking_blocked_since                          TIMESTAMP WITHOUT TIME ZONE,
  suspension                                      nw.Suspension               NOT NULL,
  suspension_active_since                         TIMESTAMP WITHOUT TIME ZONE,
  suspension_suspended_since                      TIMESTAMP WITHOUT TIME ZONE,
  details_name                                    CHARACTER VARYING           NOT NULL,
  details_description                             CHARACTER VARYING,
  location_url                                    CHARACTER VARYING           NOT NULL,
  category_id                                     INT                         NOT NULL,
  account_currency_code                           CHARACTER VARYING,
  account_settlement                              BIGINT,
  account_guarantee                               BIGINT,
  account_payout                                  BIGINT,
  contract_id                                     CHARACTER VARYING           NOT NULL,
  payout_tool_id                                  CHARACTER VARYING,
  payout_schedule_id                              INT,
  wtime                                           TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  current                                         BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT shop_pkey PRIMARY KEY (id)
);

CREATE INDEX shop_event_id on nw.shop(event_id);
CREATE INDEX shop_event_created_at on nw.shop(event_created_at);
CREATE INDEX shop_shop_id on nw.shop(shop_id);
CREATE INDEX shop_party_id on nw.shop(party_id);
CREATE INDEX shop_created_at on nw.shop(created_at);

-- payout --

CREATE TYPE nw.PayoutStatus AS ENUM ('unpaid', 'paid', 'cancelled', 'confirmed');
CREATE TYPE nw.PayoutPaidStatusDetails AS ENUM ('card_details', 'account_details');
CREATE TYPE nw.UserType AS ENUM ('internal_user', 'external_user', 'service_user');
CREATE TYPE nw.PayoutType AS ENUM ('bank_card', 'bank_account');
CREATE TYPE nw.PayoutAccountType AS ENUM ('russian_payout_account', 'international_payout_account');

CREATE TABLE nw.payout(
  id                                                         BIGSERIAL                   NOT NULL,
  event_id                                                   BIGINT                      NOT NULL,
  event_created_at                                           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  payout_id                                                  CHARACTER VARYING           NOT NULL,
  party_id                                                   CHARACTER VARYING           NOT NULL,
  shop_id                                                    CHARACTER VARYING           NOT NULL,
  contract_id                                                CHARACTER VARYING           NOT NULL,
  created_at                                                 TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  status                                                     nw.PayoutStatus             NOT NULL,
  status_paid_details                                        nw.PayoutPaidStatusDetails,
  status_paid_details_card_provider_name                     CHARACTER VARYING,
  status_paid_details_card_provider_transaction_id           CHARACTER VARYING,
  status_cancelled_user_info_id                              CHARACTER VARYING,
  status_cancelled_user_info_type                            nw.UserType,
  status_cancelled_details                                   CHARACTER VARYING,
  status_confirmed_user_info_id                              CHARACTER VARYING,
  status_confirmed_user_info_type                            nw.UserType,
  type                                                       nw.PayoutType               NOT NULL,
  type_card_token                                            CHARACTER VARYING,
  type_card_payment_system                                   CHARACTER VARYING,
  type_card_bin                                              CHARACTER VARYING,
  type_card_masked_pan                                       CHARACTER VARYING,
  type_card_token_provider                                   CHARACTER VARYING,
  type_account_type                                          nw.PayoutAccountType,
  type_account_russian_account                               CHARACTER VARYING,
  type_account_russian_bank_name                             CHARACTER VARYING,
  type_account_russian_bank_post_account                     CHARACTER VARYING,
  type_account_russian_bank_bik                              CHARACTER VARYING,
  type_account_russian_inn                                   CHARACTER VARYING,
  type_account_international_account_holder                  CHARACTER VARYING,
  type_account_international_bank_name                       CHARACTER VARYING,
  type_account_international_bank_address                    CHARACTER VARYING,
  type_account_international_iban                            CHARACTER VARYING,
  type_account_international_bic                             CHARACTER VARYING,
  type_account_international_local_bank_code                 CHARACTER VARYING,
  type_account_international_legal_entity_legal_name         CHARACTER VARYING,
  type_account_international_legal_entity_trading_name       CHARACTER VARYING,
  type_account_international_legal_entity_registered_address CHARACTER VARYING,
  type_account_international_legal_entity_actual_address     CHARACTER VARYING,
  type_account_international_legal_entity_registered_number  CHARACTER VARYING,
  type_account_purpose                                       CHARACTER VARYING,
  type_account_legal_agreement_signed_at                     TIMESTAMP WITHOUT TIME ZONE,
  type_account_legal_agreement_id                            CHARACTER VARYING,
  type_account_legal_agreement_valid_until                   TIMESTAMP WITHOUT TIME ZONE,
  initiator_id                                               CHARACTER VARYING           NOT NULL,
  initiator_type                                             nw.UserType                 NOT NULL,
  wtime                                                      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  current                                                    BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT payout_pkey PRIMARY KEY (id)
);

CREATE INDEX payout_event_id on nw.payout(event_id);
CREATE INDEX payout_event_created_at on nw.payout(event_created_at);
CREATE INDEX payout_payout_id on nw.payout(payout_id);
CREATE INDEX payout_party_id on nw.payout(party_id);
CREATE INDEX payout_created_at on nw.payout(created_at);
CREATE INDEX payout_status on nw.payout(status);

CREATE TABLE nw.payout_summary(
  id                     BIGSERIAL                   NOT NULL,
  pyt_id                 BIGINT                      NOT NULL,
  amount                 BIGINT                      NOT NULL,
  fee                    BIGINT                      NOT NULL,
  currency_code          CHARACTER VARYING           NOT NULL,
  from_time              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  to_time                TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  operation_type         CHARACTER VARYING           NOT NULL,
  count                  INT                         NOT NULL,
  CONSTRAINT payout_summary_pkey PRIMARY KEY (id),
  CONSTRAINT fk_summary_to_payout FOREIGN KEY (pyt_id) REFERENCES nw.payout(id)
);

CREATE INDEX payout_summary_idx on nw.payout_summary(pyt_id);