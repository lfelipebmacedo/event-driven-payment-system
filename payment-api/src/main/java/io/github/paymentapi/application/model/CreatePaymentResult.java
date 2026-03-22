package io.github.paymentapi.application.model;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentResult(
        UUID paymentId,
        UUID externalReference,
        BigDecimal amount,
        String currency,
        UUID payerId,
        UUID receiverId
) {
    public static CreatePaymentResult with(UUID paymentId, UUID externalReference, BigDecimal amount, String currency, UUID payerId, UUID receiverId) {
        return new CreatePaymentResult(paymentId, externalReference, amount, currency, payerId, receiverId);
    }
}
