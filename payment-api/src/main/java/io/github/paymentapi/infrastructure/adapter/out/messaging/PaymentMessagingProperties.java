package io.github.paymentapi.infrastructure.adapter.out.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentMessagingProperties {
    public static final String PAYMENT_CREATED_QUEUE = "payment.created.queue";
    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String PAYMENT_CREATED_ROUTING_KEY = "payment.created";

    @Bean
    Queue queue() {
        return new Queue(PAYMENT_CREATED_QUEUE, true);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    Binding paymentCreatedBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(PAYMENT_CREATED_ROUTING_KEY);
    }

    @Bean
    MessageConverter jacksonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
