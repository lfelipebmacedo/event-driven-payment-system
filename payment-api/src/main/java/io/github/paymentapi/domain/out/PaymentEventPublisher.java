package io.github.paymentapi.domain.out;

import io.github.paymentapi.domain.model.Payment;

public interface PaymentEventPublisher {
    void publishPaymentCreated(Payment payment);
}
