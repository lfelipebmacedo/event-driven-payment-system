package io.github.paymentapi.domain.model;

import io.github.paymentapi.domain.exception.IdempotencyConflictException;
import io.github.paymentapi.domain.exception.PaymentDomainException;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public record Payment(
        UUID paymentId,
        UUID externalReference,
        BigDecimal amount,
        String currency,
        UUID payerId,
        UUID receiverId,
        UUID idempotencyKey,
        PaymentStatus status
) {
    public Payment {
        externalReference = requireNonNull(externalReference, "externalReference is required");
        amount = requirePositiveAmount(amount);
        currency = normalizeCurrency(currency);
        payerId = requireNonNull(payerId, "payerId is required");
        receiverId = requireNonNull(receiverId, "receiverId is required");
        idempotencyKey = requireNonNull(idempotencyKey, "idempotencyKey is required");
        status = requireNonNull(status, "status is required");

        if (payerId.equals(receiverId)) {
            throw new PaymentDomainException("payerId and receiverId must be different");
        }
    }

    public static Payment create(
            UUID paymentId,
            UUID externalReference,
            BigDecimal amount,
            String currency,
            UUID payerId,
            UUID receiverId,
            UUID idempotencyKey
    ) {
        return new Payment(
                paymentId,
                externalReference,
                amount,
                currency,
                payerId,
                receiverId,
                idempotencyKey,
                PaymentStatus.PENDING
        );
    }

    public static Payment with(UUID paymentId, UUID externalReference, BigDecimal amount, String currency, UUID payerId, UUID receiverId, UUID idempotencyKey, PaymentStatus status) {
        return new Payment(paymentId, externalReference, amount, currency, payerId, receiverId, idempotencyKey, status);
    }

    public void ensureSameBusinessRequestAs(Payment other) {
        if (!hasSameBusinessRequestAs(other)) {
            throw new IdempotencyConflictException("idempotencyKey is already associated with a different payment request");
        }
    }

    private boolean hasSameBusinessRequestAs(Payment other) {
        return externalReference.equals(other.externalReference)
                && amount.compareTo(other.amount) == 0
                && currency.equals(other.currency)
                && payerId.equals(other.payerId)
                && receiverId.equals(other.receiverId);
    }

    private static UUID requireNonNull(UUID value, String message) {
        if (value == null) {
            throw new PaymentDomainException(message);
        }
        return value;
    }

    private static PaymentStatus requireNonNull(PaymentStatus value, String message) {
        if (value == null) {
            throw new PaymentDomainException(message);
        }
        return value;
    }

    private static BigDecimal requirePositiveAmount(BigDecimal amount) {
        if (amount == null) {
            throw new PaymentDomainException("amount is required");
        }
        if (amount.signum() <= 0) {
            throw new PaymentDomainException("amount must be greater than zero");
        }
        return amount;
    }

    private static String normalizeCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            throw new PaymentDomainException("currency is required");
        }

        String normalizedCurrency = currency.trim().toUpperCase();

        try {
            return Currency.getInstance(normalizedCurrency).getCurrencyCode();
        } catch (IllegalArgumentException exception) {
            throw new PaymentDomainException("currency must be a valid ISO-4217 code");
        }
    }
}
