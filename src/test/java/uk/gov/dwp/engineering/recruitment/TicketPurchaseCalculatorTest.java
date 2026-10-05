package uk.gov.dwp.engineering.recruitment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.dwp.engineering.recruitment.domain.TicketType.ADULT;
import static uk.gov.dwp.engineering.recruitment.domain.TicketType.CHILD;
import static uk.gov.dwp.engineering.recruitment.domain.TicketType.INFANT;;

class TicketPurchaseCalculatorTest {

    private TicketPurchaseCalculator calculator;

    @BeforeEach
    void setUp() {
        TicketPriceCalculator ticketPriceCalculator =
                new TicketPriceCalculator();

        calculator =
                new TicketPurchaseCalculator(ticketPriceCalculator);
    }

    @Test
    void givenMixedTickets_whenCalculating_thenReturnsCorrectSummary() {

        TicketRequest adultTickets =
                new TicketRequest(ADULT, 2);

        TicketRequest childTickets =
                new TicketRequest(CHILD, 2);

        TicketRequest infantTickets =
                new TicketRequest(INFANT, 1);

        TicketPurchaseSummary summary =
                calculator.calculate(
                        adultTickets,
                        childTickets,
                        infantTickets
                );

        assertEquals(2, summary.adultCount());
        assertEquals(2, summary.childCount());
        assertEquals(1, summary.infantCount());

        assertEquals(5, summary.totalTicketCount());


        assertEquals(4L, summary.seatCount());


        assertEquals(
                new BigDecimal("86.98"),
                summary.totalPrice()
        );
    }
}