package io.github.paymentapi.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataJpaRepository extends JpaRepository<PaymentJpaEntity, UUID> {
}
