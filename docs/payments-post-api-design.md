# POST /payments API Design

This document defines the proposed contract for payment creation in `payment-api`.

The design is intentionally aligned with the current application model:

- `CreatePaymentCommand`
- `Payment`
- idempotent retries using `Idempotency-Key`
- asynchronous processing after initial creation

## Endpoint

```http
POST /payments
Content-Type: application/json
Idempotency-Key: <uuid>
```

## Purpose

Creates a new payment request, stores it with initial status `PENDING`, and publishes a `payment.created` event for downstream asynchronous processing.

If the same `Idempotency-Key` is reused with the same logical request, the API should return the existing payment instead of creating a duplicate record.

## Entry Data

### Headers

| Header | Required | Type | Description |
|---|---|---|---|
| `Idempotency-Key` | Yes | UUID | Unique key used to make client retries safe. |
| `Content-Type` | Yes | string | Must be `application/json`. |

### Request Body

```json
{
  "externalReference": "5aa4cfaf-9bc4-45dd-a2d1-2d1194d80c89",
  "amount": 1500.00,
  "currency": "BRL",
  "payerId": "df287b4b-980a-442d-96de-29044f15de13",
  "receiverId": "968def22-7b1a-4bec-a08c-4df8afbb3208"
}
```

| Field | Required | Type | Description |
|---|---|---|---|
| `externalReference` | Yes | UUID | External business reference for correlation with the client system. |
| `amount` | Yes | decimal | Payment amount. Must be greater than `0`. |
| `currency` | Yes | string | ISO-4217 currency code such as `BRL` or `USD`. |
| `payerId` | Yes | UUID | Unique identifier of the account or user sending funds. |
| `receiverId` | Yes | UUID | Unique identifier of the account or user receiving funds. |

## Result Data

### Success Response

For a newly created payment, the API should return `201 Created`.

For an idempotent retry that matches an existing payment, the API should return `200 OK`.

```json
{
  "paymentId": "1ce58802-0310-4446-a18d-bd38377128d7",
  "externalReference": "5aa4cfaf-9bc4-45dd-a2d1-2d1194d80c89",
  "idempotencyKey": "43432db4-a00e-4fdd-aaf1-6d81d4ff676c",
  "status": "PENDING"
}
```

The response intentionally returns only the minimum fields a client typically needs after creation:

- the internal payment identifier
- the client correlation identifier
- the idempotency key used for safe retry handling
- the current payment lifecycle status

| Field | Type | Description |
|---|---|---|
| `paymentId` | UUID | Internal identifier of the created payment. |
| `externalReference` | UUID | Client-provided business reference. |
| `idempotencyKey` | UUID | Key used for safe retries. |
| `status` | `PaymentStatus` | Initial lifecycle state. Expected initial value: `PENDING`. Allowed values: `PENDING`, `PROCESSING`, `SUCCEEDED`, `FAILED`. |

## Suggested Behavior Rules

1. Validate the JSON payload and reject malformed data with `400 Bad Request`.
2. Validate business constraints such as positive amount, supported currency, and distinct payer/receiver.
3. Validate that `Idempotency-Key` is present and is a valid UUID.
4. If the idempotency key already exists, return the previously created payment.
5. If the payment is new, persist it first and publish the domain event only after persistence succeeds.

## Error Response Shape

Suggested error payload:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "amount must be greater than zero"
}
```

Common cases:

| HTTP Status | Code | When |
|---|---|---|
| `400` | `VALIDATION_ERROR` | Invalid body, invalid UUID, invalid amount, unsupported currency. |
| `409` | `IDEMPOTENCY_CONFLICT` | Same idempotency key reused with a different logical request. |
| `415` | `UNSUPPORTED_MEDIA_TYPE` | `Content-Type` is not `application/json`. |
| `500` | `INTERNAL_ERROR` | Unexpected failure while creating the payment. |

## Notes

- This proposal uses camelCase JSON names because the current Java records use camelCase fields.
- This proposal uses UUID values for all identifiers because `CreatePaymentCommand` currently models them as `UUID`.
- The `Payment` model should use a `PaymentStatus` enum rather than a raw `String` for status values.
- Suggested `PaymentStatus` values: `PENDING`, `PROCESSING`, `SUCCEEDED`, `FAILED`.
- `POST /payments` should only return the initial persisted state, which is expected to be `PENDING`.
