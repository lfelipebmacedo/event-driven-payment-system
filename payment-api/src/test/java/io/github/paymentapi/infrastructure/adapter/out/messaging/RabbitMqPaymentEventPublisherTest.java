package io.github.paymentapi.infrastructure.adapter.out.messaging;

import io.github.paymentapi.domain.model.Payment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RabbitMqPaymentEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitMqPaymentEventPublisher publisher;

    @Test
    void shouldPublishPaymentCreatedEventToConfiguredExchange() {
        Payment payment = Payment.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("25.00"),
                "BRL",
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        ArgumentCaptor<PaymentCreatedEvent> eventCaptor = ArgumentCaptor.forClass(PaymentCreatedEvent.class);

        publisher.publishPaymentCreated(payment);

        verify(rabbitTemplate).convertAndSend(
                org.mockito.ArgumentMatchers.eq(PaymentMessagingProperties.PAYMENT_EXCHANGE),
                org.mockito.ArgumentMatchers.eq(PaymentMessagingProperties.PAYMENT_CREATED_ROUTING_KEY),
                eventCaptor.capture()
        );
        assertEquals(payment.paymentId(), eventCaptor.getValue().paymentId());
        assertEquals(payment.status(), eventCaptor.getValue().status());
    }
}
