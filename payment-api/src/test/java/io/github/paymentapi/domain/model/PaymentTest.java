package io.github.paymentapi.domain.model;

import io.github.paymentapi.domain.exception.IdempotencyConflictException;
import io.github.paymentapi.domain.exception.PaymentDomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentTest {

    @Test
    void shouldCreatePaymentWithPendingStatusAndNormalizedCurrency() {
        Payment payment = Payment.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("1500.00"),
                "brl",
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        assertEquals("BRL", payment.currency());
        assertEquals(PaymentStatus.PENDING, payment.status());
    }

    @Test
    void shouldRejectPaymentWithAmountLessThanOrEqualToZero() {
        PaymentDomainException exception = assertThrows(
                PaymentDomainException.class,
                () -> Payment.create(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        BigDecimal.ZERO,
                        "BRL",
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID()
                )
        );

        assertEquals("amount must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldRejectPaymentWithInvalidCurrency() {
        PaymentDomainException exception = assertThrows(
                PaymentDomainException.class,
                () -> Payment.create(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        new BigDecimal("10.00"),
                        "REAL",
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID()
                )
        );

        assertEquals("currency must be a valid ISO-4217 code", exception.getMessage());
    }

    @Test
    void shouldRejectPaymentWhenPayerAndReceiverAreTheSame() {
        UUID accountId = UUID.randomUUID();

        PaymentDomainException exception = assertThrows(
                PaymentDomainException.class,
                () -> Payment.create(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        new BigDecimal("10.00"),
                        "BRL",
                        accountId,
                        accountId,
                        UUID.randomUUID()
                )
        );

        assertEquals("payerId and receiverId must be different", exception.getMessage());
    }

    @Test
    void shouldAllowIdempotentRetryForSameBusinessRequest() {
        UUID externalReference = UUID.randomUUID();
        UUID payerId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        UUID idempotencyKey = UUID.randomUUID();

        Payment existingPayment = Payment.create(
                UUID.randomUUID(),
                externalReference,
                new BigDecimal("10.00"),
                "BRL",
                payerId,
                receiverId,
                idempotencyKey
        );

        Payment retriedPayment = Payment.create(
                UUID.randomUUID(),
                externalReference,
                new BigDecimal("10.0"),
                "BRL",
                payerId,
                receiverId,
                idempotencyKey
        );

        assertDoesNotThrow(() -> existingPayment.ensureSameBusinessRequestAs(retriedPayment));
    }

    @Test
    void shouldRejectIdempotentRetryWhenBusinessRequestChanges() {
        UUID payerId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        UUID idempotencyKey = UUID.randomUUID();

        Payment existingPayment = Payment.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("10.00"),
                "BRL",
                payerId,
                receiverId,
                idempotencyKey
        );

        Payment changedPayment = Payment.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("10.00"),
                "BRL",
                payerId,
                receiverId,
                idempotencyKey
        );

        IdempotencyConflictException exception = assertThrows(
                IdempotencyConflictException.class,
                () -> existingPayment.ensureSameBusinessRequestAs(changedPayment)
        );

        assertEquals("idempotencyKey is already associated with a different payment request", exception.getMessage());
    }
}
