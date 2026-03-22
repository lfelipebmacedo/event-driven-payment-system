package io.github.paymentapi.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentStatusTest {

    private static final int STATUS_COLUMN_LENGTH = 20;

    @Test
    void shouldKeepEnumNamesCompatibleWithDatabaseColumnLength() {
        for (PaymentStatus status : PaymentStatus.values()) {
            assertTrue(
                    status.name().length() <= STATUS_COLUMN_LENGTH,
                    () -> "PaymentStatus '%s' exceeds the database column length of %d"
                            .formatted(status.name(), STATUS_COLUMN_LENGTH)
            );
        }
    }
}
