package uk.gov.dwp.engineering.recruitment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicketPurchaseValidatorTest {

    private TicketPurchaseValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TicketPurchaseValidator();
    }

    @Test
    void givenExactlyTwentyFiveTickets_whenValidatingBusinessRules_thenDoesNotThrow() {

        TicketPurchaseSummary summary =
                new TicketPurchaseSummary(
                        25,
                        0,
                        0,
                        25L,
                        new BigDecimal("649.75")
                );

        assertDoesNotThrow(
                () -> validator.validateBusinessRules(summary)
        );
    }

    @Test
    void givenMoreThanTwentyFiveTickets_whenValidatingBusinessRules_thenThrowsInvalidBookingException() {

        TicketPurchaseSummary summary =
                new TicketPurchaseSummary(
                        26,
                        0,
                        0,
                        26L,
                        new BigDecimal("675.74")
                );

        assertThrows(
                InvalidBookingException.class,
                () -> validator.validateBusinessRules(summary)
        );
    }

    @Test
    void givenMoreInfantsThanAdults_whenValidatingBusinessRules_thenThrowsInvalidBookingException() {

        TicketPurchaseSummary summary =
                new TicketPurchaseSummary(
                        1,
                        0,
                        2,
                        1L,
                        new BigDecimal("25.99")
                );

        assertThrows(
                InvalidBookingException.class,
                () -> validator.validateBusinessRules(summary)
        );
    }
}
