package io.github.paymentapi.application.service;

import io.github.paymentapi.application.model.CreatePaymentCommand;
import io.github.paymentapi.application.model.CreatePaymentResult;
import io.github.paymentapi.application.port.in.CreatePayment;
import io.github.paymentapi.domain.model.Payment;
import io.github.paymentapi.domain.out.PaymentEventPublisher;
import io.github.paymentapi.domain.out.PaymentRepository;

import java.util.UUID;

public class CreatePaymentService implements CreatePayment {

    private final PaymentRepository repository;
    private final PaymentEventPublisher eventPublisher;

    public CreatePaymentService(PaymentRepository repository, PaymentEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CreatePaymentResult execute(CreatePaymentCommand createPaymentCommand) {
        Payment requestedPayment = Payment.create(
                UUID.randomUUID(),
                createPaymentCommand.externalReference(),
                createPaymentCommand.amount(),
                createPaymentCommand.currency(),
                createPaymentCommand.payerId(),
                createPaymentCommand.receiverId(),
                createPaymentCommand.idempotencyKey()
        );

        return repository.findIdempotencyKey(requestedPayment.idempotencyKey())
                .map(existingPayment -> reuseExistingPayment(existingPayment, requestedPayment))
                .orElseGet(() -> createAndPublish(requestedPayment));
    }

    private CreatePaymentResult reuseExistingPayment(Payment existingPayment, Payment requestedPayment) {
        existingPayment.ensureSameBusinessRequestAs(requestedPayment);
        return toResult(existingPayment);
    }

    private CreatePaymentResult createAndPublish(Payment requestedPayment) {
        Payment persistedPayment = repository.save(requestedPayment);
        eventPublisher.publishPaymentCreated(persistedPayment);
        return toResult(persistedPayment);
    }

    private CreatePaymentResult toResult(Payment payment) {
        return CreatePaymentResult.with(
                payment.paymentId(),
                payment.externalReference(),
                payment.amount(),
                payment.currency(),
                payment.payerId(),
                payment.receiverId()
        );
    }
}
