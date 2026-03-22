package io.github.paymentapi.application.model;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentCommand(
        UUID externalReference,
        BigDecimal amount,
        String currency,
        UUID payerId,
        UUID receiverId,
        UUID idempotencyKey
) {
    public static CreatePaymentCommand with(UUID externalReference, BigDecimal amount, String currency, UUID payerId, UUID receiverId, UUID idempotencyKey) {
        return new CreatePaymentCommand(externalReference, amount, currency, payerId, receiverId, idempotencyKey);
    }
}
