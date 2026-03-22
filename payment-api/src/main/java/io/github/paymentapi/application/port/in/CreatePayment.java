package io.github.paymentapi.application.port.in;

import io.github.paymentapi.application.model.CreatePaymentCommand;
import io.github.paymentapi.application.model.CreatePaymentResult;

public interface CreatePayment {
    CreatePaymentResult execute(CreatePaymentCommand command);
}
