package io.github.paymentapi.infrastructure.adapter.in.controller;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(UUID externalReference,
                                   BigDecimal amount,
                                   String currency,
                                   UUID payerId,
                                   UUID receiverId) {
}
