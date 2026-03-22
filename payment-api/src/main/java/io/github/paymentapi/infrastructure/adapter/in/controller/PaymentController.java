package io.github.paymentapi.infrastructure.adapter.in.controller;

import io.github.paymentapi.application.model.CreatePaymentCommand;
import io.github.paymentapi.application.model.CreatePaymentResult;
import io.github.paymentapi.application.port.in.CreatePayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final CreatePayment createPayment;

    @Autowired
    public PaymentController(CreatePayment createPayment) {
        this.createPayment = createPayment;
    }

    @PostMapping
    public ResponseEntity<CreatePaymentResponse> createPayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody CreatePaymentRequest request) throws URISyntaxException {
        CreatePaymentResult createPaymentResult = createPayment.execute(CreatePaymentCommand.with(request.externalReference(), request.amount(), request.currency(),
                request.payerId(), request.receiverId(), parseIdempotencyKey(idempotencyKey)));

        CreatePaymentResponse response = CreatePaymentResponse.with(
                createPaymentResult.externalReference(),
                createPaymentResult.amount(),
                createPaymentResult.currency(),
                createPaymentResult.payerId(),
                createPaymentResult.receiverId());

        if (createPaymentResult.created()) {
            return ResponseEntity.created(new URI(createPaymentResult.paymentId().toString())).body(response);
        }

        return ResponseEntity.ok(response);
    }

    private UUID parseIdempotencyKey(String idempotencyKey) {
        try {
            return UUID.fromString(idempotencyKey);
        } catch (IllegalArgumentException exception) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST);
        }
    }
}
