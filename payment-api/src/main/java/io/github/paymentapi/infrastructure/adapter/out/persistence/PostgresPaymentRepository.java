package io.github.paymentapi.infrastructure.adapter.out.persistence;

import io.github.paymentapi.domain.model.Payment;
import io.github.paymentapi.domain.model.PaymentStatus;
import io.github.paymentapi.domain.out.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class PostgresPaymentRepository implements PaymentRepository {

    private final SpringDataJpaRepository repository;

    @Autowired
    public PostgresPaymentRepository(SpringDataJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Payment> findIdempotencyKey(UUID idempotencyKey) {
        return Optional.empty();
    }

    @Override
    public Payment save(Payment payment) {
        PaymentJpaEntity paymentEntity = PaymentJpaEntity.create(payment.paymentId(),
                payment.externalReference(),
                payment.amount(),
                payment.currency(),
                payment.payerId(),
                payment.receiverId(),
                payment.idempotencyKey());

        PaymentJpaEntity entity = repository.save(paymentEntity);

        return Payment.with(entity.getPaymentId(),
                entity.getExternalReference(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getPayerId(),
                entity.getReceiverId(),
                entity.getIdempotencyKey(),
                entity.getStatus()
        );
    }
}
