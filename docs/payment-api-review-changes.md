# Payment API Review Changes

This document explains the changes made after review feedback on the new `POST /payments` flow.

## Context

The review identified three functional issues in the payment creation flow:

1. The persistence adapter did not look up existing payments by `Idempotency-Key`.
2. A malformed `Idempotency-Key` header caused a server error instead of a client error.
3. The API always returned `201 Created`, even for idempotent replays.

These issues affected the idempotency contract of the endpoint.

## What Changed

### 1. Repository now resolves existing payments by idempotency key

`PostgresPaymentRepository` now calls `SpringDataJpaRepository.findByIdempotencyKey(...)` and maps the stored JPA entity back to the domain `Payment`.

Before this change:

- `findIdempotencyKey(...)` always returned `Optional.empty()`
- every retry was treated as a brand new request
- the second request with the same key could hit the database unique constraint and fail

After this change:

- retries can reuse the original persisted payment
- the application service can correctly detect safe idempotent replays
- business conflicts still surface through the existing domain validation

## 2. Application result now distinguishes create vs replay

`CreatePaymentResult` now carries a `created` flag.

`CreatePaymentService` sets:

- `created = true` when a new payment is persisted and published
- `created = false` when an existing payment is reused for an idempotent retry

This keeps the HTTP decision explicit instead of forcing the controller to infer replay behavior indirectly.

## 3. Controller now returns correct HTTP status codes

`PaymentController` now behaves as follows:

- `201 Created` when a payment is newly created
- `200 OK` when the request is a valid idempotent replay
- `400 Bad Request` when `Idempotency-Key` is not a valid UUID

The controller parses the header explicitly and converts `UUID.fromString(...)` failures into a bad request response.

## Updated Request Semantics

### New payment

If the request uses a new `Idempotency-Key`, the API:

1. creates a new payment
2. stores it
3. publishes the `payment.created` event
4. returns `201 Created`

### Idempotent replay

If the same `Idempotency-Key` is retried with the same business payload, the API:

1. loads the existing payment
2. skips creating a duplicate row
3. skips publishing a duplicate event
4. returns `200 OK`

### Malformed key

If the header is missing a valid UUID format, the API returns `400 Bad Request` instead of surfacing an internal server error.

## Test Coverage Added

The review fixes are covered by focused tests:

- `PaymentControllerTest`
  - invalid idempotency key returns `400`
  - newly created payment returns `201`
  - idempotent replay returns `200`
- `PostgresPaymentRepositoryTest`
  - repository lookup returns an existing payment by idempotency key
- `CreatePaymentServiceTest`
  - asserts the `created` flag for both new and replayed requests

## Files Changed

- `payment-api/src/main/java/io/github/paymentapi/infrastructure/adapter/out/persistence/PostgresPaymentRepository.java`
- `payment-api/src/main/java/io/github/paymentapi/infrastructure/adapter/out/persistence/SpringDataJpaRepository.java`
- `payment-api/src/main/java/io/github/paymentapi/application/model/CreatePaymentResult.java`
- `payment-api/src/main/java/io/github/paymentapi/application/service/CreatePaymentService.java`
- `payment-api/src/main/java/io/github/paymentapi/infrastructure/adapter/in/controller/PaymentController.java`
- `payment-api/src/test/java/io/github/paymentapi/infrastructure/adapter/in/controller/PaymentControllerTest.java`
- `payment-api/src/test/java/io/github/paymentapi/infrastructure/adapter/out/persistence/PostgresPaymentRepositoryTest.java`
- `payment-api/src/test/java/io/github/paymentapi/application/service/CreatePaymentServiceTest.java`

## Outcome

The payment creation endpoint now honors the intended idempotency behavior:

- safe retries return the original result
- malformed client input is rejected as `400`
- HTTP status codes differentiate new creation from replay
