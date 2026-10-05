package uk.gov.dwp.engineering.recruitment;

import java.math.BigDecimal;

public record TicketPurchaseSummary(
        int adultCount,
        int childCount,
        int infantCount,
        long seatCount,
        BigDecimal totalPrice
) {

    public int totalTicketCount() {
        return adultCount + childCount + infantCount;
    }
}