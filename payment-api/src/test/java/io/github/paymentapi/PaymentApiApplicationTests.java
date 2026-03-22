package io.github.paymentapi;

import io.github.paymentapi.domain.payment.port.out.PaymentEventPublisher;
import io.github.paymentapi.domain.payment.port.out.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
    classes = PaymentApiApplication.class,
    properties = {
        "spring.autoconfigure.exclude="
            + "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
            + "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,"
            + "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration"
    }
)
class PaymentApiApplicationTests {

    @MockitoBean
    PaymentRepository paymentRepository;

    @MockitoBean
    PaymentEventPublisher paymentEventPublisher;

    @Test
    void contextLoads() {
    }

}
