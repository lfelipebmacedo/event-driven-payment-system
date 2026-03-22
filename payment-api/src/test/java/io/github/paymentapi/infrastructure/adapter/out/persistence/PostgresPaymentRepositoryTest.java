package io.github.paymentapi.infrastructure.adapter.out.persistence;

import io.github.paymentapi.domain.model.Payment;
import io.github.paymentapi.domain.model.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostgresPaymentRepositoryTest {

    @Mock
    private SpringDataJpaRepository repository;

    @InjectMocks
    private PostgresPaymentRepository paymentRepository;

    @Test
    void shouldFindPaymentByIdempotencyKey() {
        UUID idempotencyKey = UUID.randomUUID();
        PaymentJpaEntity entity = new PaymentJpaEntity();
        entity.setPaymentId(UUID.randomUUID());
        entity.setExternalReference(UUID.randomUUID());
        entity.setAmount(new BigDecimal("25.00"));
        entity.setCurrency("BRL");
        entity.setPayerId(UUID.randomUUID());
        entity.setReceiverId(UUID.randomUUID());
        entity.setIdempotencyKey(idempotencyKey);
        entity.setStatus(PaymentStatus.PENDING);

        when(repository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(entity));

        Optional<Payment> result = paymentRepository.findIdempotencyKey(idempotencyKey);

        assertTrue(result.isPresent());
        assertEquals(entity.getPaymentId(), result.get().paymentId());
        assertEquals(entity.getIdempotencyKey(), result.get().idempotencyKey());
        verify(repository).findByIdempotencyKey(idempotencyKey);
    }
}
