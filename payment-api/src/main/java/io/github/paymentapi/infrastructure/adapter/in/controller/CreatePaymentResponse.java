package io.github.paymentapi.infrastructure.adapter.in.controller;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentResponse(UUID externalReference,
                                    BigDecimal amount,
                                    String currency,
                                    UUID payerId,
                                    UUID receiverId) {
    public static CreatePaymentResponse with(UUID externalReference, BigDecimal amount, String currency, UUID payerId, UUID receiverId) {
        return new CreatePaymentResponse(externalReference, amount, currency, payerId, receiverId);
    }
}
