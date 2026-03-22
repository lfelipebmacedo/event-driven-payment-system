package io.github.paymentapi.domain.exception;

public class IdempotencyConflictException extends PaymentDomainException {

    public IdempotencyConflictException(String message) {
        super(message);
    }
}
