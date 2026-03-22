package io.github.paymentapi.infrastructure.adapter.out.messaging;

import io.github.paymentapi.domain.model.Payment;
import io.github.paymentapi.domain.model.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCreatedEvent(
        UUID paymentId,
        UUID externalReference,
        BigDecimal amount,
        String currency,
        UUID payerId,
        UUID receiverId,
        UUID idempotencyKey,
        PaymentStatus status
) {
    public static PaymentCreatedEvent from(Payment payment) {
        return new PaymentCreatedEvent(
                payment.paymentId(),
                payment.externalReference(),
                payment.amount(),
                payment.currency(),
                payment.payerId(),
                payment.receiverId(),
                payment.idempotencyKey(),
                payment.status()
        );
    }
}
