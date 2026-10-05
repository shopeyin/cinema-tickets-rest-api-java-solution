package uk.gov.dwp.engineering.recruitment;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.domain.TicketType;

@Component
public class TicketPurchaseCalculator {

    private final TicketPriceCalculator ticketPriceCalculator;

    public TicketPurchaseCalculator(
            TicketPriceCalculator ticketPriceCalculator) {
        this.ticketPriceCalculator = ticketPriceCalculator;
    }

    public TicketPurchaseSummary calculate(TicketRequest... ticketRequests) {

        BigDecimal totalPrice = BigDecimal.ZERO;

        long seatCount = 0;
        int adultCount = 0;
        int childCount = 0;
        int infantCount = 0;

        for (TicketRequest ticketRequest : ticketRequests) {

            int ticketCount = ticketRequest.ticketCount();

            switch (ticketRequest.type()) {
                case ADULT -> adultCount += ticketCount;
                case CHILD -> childCount += ticketCount;
                case INFANT -> infantCount += ticketCount;
            }

            BigDecimal ticketPrice =
                    ticketPriceCalculator.getPrice(
                            ticketRequest.type()
                    );

            totalPrice = totalPrice.add(
                    ticketPrice.multiply(
                            BigDecimal.valueOf(ticketCount)
                    )
            );

            if (ticketRequest.type() != TicketType.INFANT) {
                seatCount += ticketCount;
            }
        }

        return new TicketPurchaseSummary(
                adultCount,
                childCount,
                infantCount,
                seatCount,
                totalPrice
        );
    }
}