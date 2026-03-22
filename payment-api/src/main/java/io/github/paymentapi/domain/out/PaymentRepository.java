package io.github.paymentapi.domain.out;

import io.github.paymentapi.domain.model.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Optional<Payment> findIdempotencyKey(UUID idempotencyKey);

    Payment save(Payment payment);
}
