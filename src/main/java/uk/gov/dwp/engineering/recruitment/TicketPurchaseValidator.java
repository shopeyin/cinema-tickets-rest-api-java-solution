package uk.gov.dwp.engineering.recruitment;

import org.springframework.stereotype.Component;
import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;

@Component
public class TicketPurchaseValidator {

    public void validateAccount(Long accountId) {
        if (accountId == null || accountId <= 0) {
            throw new InvalidBookingException(
                    "Account ID must be greater than zero"
            );
        }
    }

    public void validateTicketRequests(TicketRequest[] ticketRequests) {
        if (ticketRequests == null) {
            throw new InvalidBookingException(
                    "Ticket requests must not be null"
            );
        }

        for (TicketRequest ticketRequest : ticketRequests) {

            if (ticketRequest == null) {
                throw new InvalidBookingException(
                        "Ticket request must not be null"
                );
            }

            if (ticketRequest.type() == null) {
                throw new InvalidBookingException(
                        "Ticket type must not be null"
                );
            }

            if (ticketRequest.ticketCount() < 0) {
                throw new InvalidBookingException(
                        "Ticket count cannot be negative"
                );
            }
        }
    }

    public void validateBusinessRules(TicketPurchaseSummary summary) {

        if (summary.totalTicketCount() == 0) {
            throw new InvalidBookingException(
                    "At least one ticket must be purchased"
            );
        }

        if (summary.totalTicketCount() > 25) {
            throw new InvalidBookingException(
                    "A maximum of 25 tickets can be purchased"
            );
        }

        if (summary.childCount() > 0 && summary.adultCount() == 0) {
            throw new InvalidBookingException(
                    "Child tickets cannot be purchased without an adult ticket"
            );
        }

        if (summary.infantCount() > summary.adultCount()) {
            throw new InvalidBookingException(
                    "The number of infant tickets cannot exceed the number of adult tickets"
            );
        }
    }
}