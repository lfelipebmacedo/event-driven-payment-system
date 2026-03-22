package io.github.paymentapi.infrastructure.adapter.out.persistence;

import io.github.paymentapi.domain.model.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "T_PAYMENT")
public class PaymentJpaEntity {
    @Id
    private UUID paymentId;
    @Column(name = "external_reference")
    private UUID externalReference;
    private BigDecimal amount;
    private String currency;
    @Column(name = "payer_id")
    private UUID payerId;
    @Column(name = "receiver_id")
    private UUID receiverId;
    @Column(name = "idempotency_key")
    private UUID idempotencyKey;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    protected PaymentJpaEntity() {
    }

    private PaymentJpaEntity(UUID paymentId, UUID externalReference, BigDecimal amount, String currency, UUID payerId, UUID receiverId, UUID idempotencyKey, PaymentStatus status) {
        this.paymentId = paymentId;
        this.externalReference = externalReference;
        this.amount = amount;
        this.currency = currency;
        this.payerId = payerId;
        this.receiverId = receiverId;
        this.idempotencyKey = idempotencyKey;
        this.status = status;
    }

    public static PaymentJpaEntity create(UUID paymentId, UUID externalReference, BigDecimal amount, String currency, UUID payerId, UUID receiverId, UUID idempotencyKey) {
        return new PaymentJpaEntity(paymentId, externalReference, amount, currency, payerId, receiverId, idempotencyKey, PaymentStatus.PENDING);
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public UUID getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(UUID externalReference) {
        this.externalReference = externalReference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public UUID getPayerId() {
        return payerId;
    }

    public void setPayerId(UUID payerId) {
        this.payerId = payerId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }

    public UUID getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(UUID idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
