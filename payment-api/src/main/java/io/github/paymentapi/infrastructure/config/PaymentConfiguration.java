package io.github.paymentapi.infrastructure.config;

import io.github.paymentapi.application.port.in.CreatePayment;
import io.github.paymentapi.application.service.CreatePaymentService;
import io.github.paymentapi.domain.out.PaymentEventPublisher;
import io.github.paymentapi.domain.out.PaymentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfiguration {

    private final PaymentRepository repository;
    private final PaymentEventPublisher eventPublisher;

    public PaymentConfiguration(PaymentRepository repository, PaymentEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Bean
    public CreatePayment createPayment() {
        return new CreatePaymentService(repository, eventPublisher);
    }
}
