package io.github.paymentapi.application.service;

import io.github.paymentapi.application.model.CreatePaymentCommand;
import io.github.paymentapi.application.model.CreatePaymentResult;
import io.github.paymentapi.domain.exception.IdempotencyConflictException;
import io.github.paymentapi.domain.model.Payment;
import io.github.paymentapi.domain.out.PaymentEventPublisher;
import io.github.paymentapi.domain.out.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatePaymentServiceTest {

    @Mock
    private PaymentRepository repository;

    @Mock
    private PaymentEventPublisher eventPublisher;

    @InjectMocks
    private CreatePaymentService service;

    @Test
    void shouldCreateAndPersistNewPayment() {
        CreatePaymentCommand command = new CreatePaymentCommand(
                UUID.randomUUID(),
                new BigDecimal("25.00"),
                "BRL",
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        Payment persistedPayment = Payment.create(
                UUID.randomUUID(),
                command.externalReference(),
                command.amount(),
                command.currency(),
                command.payerId(),
                command.receiverId(),
                command.idempotencyKey()
        );

        when(repository.findIdempotencyKey(command.idempotencyKey())).thenReturn(Optional.empty());
        when(repository.save(any(Payment.class))).thenReturn(persistedPayment);

        CreatePaymentResult result = service.execute(command);

        assertEquals(command.externalReference(), result.externalReference());
        assertEquals(command.amount(), result.amount());
        assertEquals("BRL", result.currency());
        verify(repository).findIdempotencyKey(command.idempotencyKey());
        verify(repository).save(any(Payment.class));
        verify(eventPublisher).publishPaymentCreated(persistedPayment);
    }

    @Test
    void shouldReturnExistingPaymentForMatchingIdempotentRetry() {
        UUID externalReference = UUID.randomUUID();
        UUID payerId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        UUID idempotencyKey = UUID.randomUUID();
        Payment existingPayment = Payment.create(
                UUID.randomUUID(),
                externalReference,
                new BigDecimal("25.00"),
                "BRL",
                payerId,
                receiverId,
                idempotencyKey
        );
        CreatePaymentCommand retryCommand = new CreatePaymentCommand(
                externalReference,
                new BigDecimal("25.0"),
                "BRL",
                payerId,
                receiverId,
                idempotencyKey
        );

        when(repository.findIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(existingPayment));

        CreatePaymentResult result = service.execute(retryCommand);

        assertEquals(existingPayment.externalReference(), result.externalReference());
        verify(repository).findIdempotencyKey(idempotencyKey);
        verify(repository, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publishPaymentCreated(any(Payment.class));
    }

    @Test
    void shouldRejectRetryWhenExistingPaymentDiffersFromRequestedBusinessData() {
        UUID payerId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        UUID idempotencyKey = UUID.randomUUID();
        Payment existingPayment = Payment.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("25.00"),
                "BRL",
                payerId,
                receiverId,
                idempotencyKey
        );
        CreatePaymentCommand conflictingCommand = new CreatePaymentCommand(
                UUID.randomUUID(),
                new BigDecimal("25.00"),
                "BRL",
                payerId,
                receiverId,
                idempotencyKey
        );

        when(repository.findIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(existingPayment));

        assertThrows(IdempotencyConflictException.class, () -> service.execute(conflictingCommand));
        verify(repository).findIdempotencyKey(idempotencyKey);
        verify(repository, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publishPaymentCreated(any(Payment.class));
    }
}
