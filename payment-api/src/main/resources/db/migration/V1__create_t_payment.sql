CREATE TABLE T_PAYMENT (
    payment_id UUID NOT NULL,
    external_reference UUID NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    payer_id UUID NOT NULL,
    receiver_id UUID NOT NULL,
    idempotency_key UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT pk_t_payment PRIMARY KEY (payment_id),
    CONSTRAINT uk_t_payment_idempotency_key UNIQUE (idempotency_key),
    CONSTRAINT ck_t_payment_amount_positive CHECK (amount > 0),
    CONSTRAINT ck_t_payment_currency_code_length CHECK (char_length(currency) = 3),
    CONSTRAINT ck_t_payment_distinct_parties CHECK (payer_id <> receiver_id),
    CONSTRAINT ck_t_payment_status CHECK (status IN ('PENDING', 'PROCESSING', 'SUCCEEDED', 'FAILED'))
);
