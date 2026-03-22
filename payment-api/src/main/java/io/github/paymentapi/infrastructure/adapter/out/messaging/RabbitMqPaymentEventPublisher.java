package io.github.paymentapi.infrastructure.adapter.out.messaging;

import io.github.paymentapi.domain.model.Payment;
import io.github.paymentapi.domain.out.PaymentEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqPaymentEventPublisher implements PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqPaymentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishPaymentCreated(Payment payment) {
        rabbitTemplate.convertAndSend(
                PaymentMessagingProperties.PAYMENT_EXCHANGE,
                PaymentMessagingProperties.PAYMENT_CREATED_ROUTING_KEY,
                PaymentCreatedEvent.from(payment)
        );
    }
}
