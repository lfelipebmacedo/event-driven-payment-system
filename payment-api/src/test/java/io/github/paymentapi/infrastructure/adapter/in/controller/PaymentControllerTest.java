package io.github.paymentapi.infrastructure.adapter.in.controller;

import io.github.paymentapi.application.model.CreatePaymentResult;
import io.github.paymentapi.application.port.in.CreatePayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreatePayment createPayment;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    @Test
    void shouldReturnBadRequestWhenIdempotencyKeyIsMalformed() throws Exception {
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", "not-a-uuid")
                        .content("""
                                {
                                  "externalReference": "d236ad64-cf6b-40ca-81c8-09fdc65f1f64",
                                  "amount": 25.00,
                                  "currency": "BRL",
                                  "payerId": "2f566dd8-628c-4d78-b8a8-92062b58a82b",
                                  "receiverId": "9efd9c65-cd86-48f9-8b45-1a888675f49b"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(createPayment, never()).execute(any());
    }

    @Test
    void shouldReturnCreatedWhenPaymentIsNew() throws Exception {
        UUID paymentId = UUID.randomUUID();
        UUID externalReference = UUID.randomUUID();
        UUID payerId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        when(createPayment.execute(any())).thenReturn(CreatePaymentResult.with(
                paymentId,
                externalReference,
                new BigDecimal("25.00"),
                "BRL",
                payerId,
                receiverId,
                true
        ));

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .content("""
                                {
                                  "externalReference": "%s",
                                  "amount": 25.00,
                                  "currency": "BRL",
                                  "payerId": "%s",
                                  "receiverId": "%s"
                                }
                                """.formatted(externalReference, payerId, receiverId)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", paymentId.toString()))
                .andExpect(jsonPath("$.externalReference").value(externalReference.toString()));
    }

    @Test
    void shouldReturnOkWhenPaymentIsReplayed() throws Exception {
        UUID externalReference = UUID.randomUUID();
        UUID payerId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        when(createPayment.execute(any())).thenReturn(CreatePaymentResult.with(
                UUID.randomUUID(),
                externalReference,
                new BigDecimal("25.00"),
                "BRL",
                payerId,
                receiverId,
                false
        ));

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .content("""
                                {
                                  "externalReference": "%s",
                                  "amount": 25.00,
                                  "currency": "BRL",
                                  "payerId": "%s",
                                  "receiverId": "%s"
                                }
                                """.formatted(externalReference, payerId, receiverId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalReference").value(externalReference.toString()));
    }
}
