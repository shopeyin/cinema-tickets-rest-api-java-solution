package uk.gov.dwp.engineering.recruitment;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import uk.gov.dwp.engineering.recruitment.domain.TicketType;

@Component
public class TicketPriceCalculator {

    private static final BigDecimal ADULT_PRICE = new BigDecimal("25.99");
    private static final BigDecimal CHILD_PRICE = new BigDecimal("17.50");

    public BigDecimal getPrice(TicketType ticketType) {
        return switch (ticketType) {
            case ADULT -> ADULT_PRICE;
            case CHILD -> CHILD_PRICE;
            case INFANT -> BigDecimal.ZERO;
        };
    }
}
