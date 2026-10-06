# Cinema Tickets Service

A Spring Boot REST service for purchasing cinema tickets.

## Approach

I implemented the solution incrementally using tests to verify the business rules and edge cases.

The implementation separates the main responsibilities:

- `CinemaTicketsServiceImpl` coordinates the ticket purchase.
- `TicketPurchaseValidator` handles input validation and booking rules.
- `TicketPurchaseCalculator` calculates ticket quantities, total price and required seats.
- `TicketPriceCalculator` contains the pricing rules.
- `TicketPurchaseSummary` represents the calculated purchase.

Validation is completed before calling `PaymentService` or `SeatReservationService`, ensuring invalid bookings do not result in payment or seat reservation.

## Assumptions and Ambiguities

### Infant tickets

The requirements state that an Infant sits on an Adult's lap but do not specify how many Infants one Adult can accompany.

I have assumed that each Infant requires an accompanying Adult. Therefore, the number of Infant tickets cannot exceed the number of Adult tickets.

For example:

- 1 Adult + 1 Infant is valid.
- 2 Adults + 2 Infants is valid.
- 1 Adult + 2 Infants is invalid.

### Maximum ticket limit

The maximum of 25 tickets is interpreted as the total number of tickets purchased, including Infant tickets, even though Infants do not require seats.

### Duplicate ticket requests

Multiple requests for the same ticket type are accumulated.

For example, `ADULT x 1` and `ADULT x 2` are treated as three Adult tickets.

### Zero ticket quantities

Individual zero-quantity requests contribute nothing to the booking. However, a booking where the total ticket quantity is zero is rejected.

### External services

As specified in the exercise, `PaymentService` and `SeatReservationService` are assumed to succeed once called.

## Validation and Error Handling

The implementation rejects invalid requests including:

- Invalid account IDs.
- Null ticket requests or ticket types.
- Negative ticket quantities.
- Purchases containing no tickets.
- Purchases exceeding 25 tickets.
- Child tickets without an Adult.
- Infant tickets without sufficient accompanying Adults.

Invalid bookings result in `InvalidBookingException`, which is returned by the REST API as `400 Bad Request`.

## Testing

Tests cover the main business rules, calculations and boundary conditions, including:

- Adult, Child and Infant pricing.
- Seat calculations.
- Mixed ticket purchases.
- Maximum 25-ticket boundary.
- Adult requirements for Child and Infant tickets.
- Invalid and null inputs.
- Duplicate ticket requests.
- Ensuring invalid bookings do not call the payment or seat reservation services.
- Controller responses for successful and invalid bookings.

## Running the Tests

```bash
mvn test
```

## Running the Application

The application requires Java 21 or later.

```bash
mvn spring-boot:run
```

Bookings are submitted using:

```text
POST /cinema/bookings
```