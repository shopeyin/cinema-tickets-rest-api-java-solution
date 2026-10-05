package uk.gov.dwp.engineering.recruitment;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static uk.gov.dwp.engineering.recruitment.domain.TicketType.ADULT;
import static uk.gov.dwp.engineering.recruitment.domain.TicketType.CHILD;
import static uk.gov.dwp.engineering.recruitment.domain.TicketType.INFANT;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;
import uk.gov.dwp.engineering.recruitment.thirdparty.PaymentService;
import uk.gov.dwp.engineering.recruitment.thirdparty.SeatReservationService;

@ExtendWith(MockitoExtension.class)
class CinemaTicketsServiceImplTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private SeatReservationService seatReservationService;


    private CinemaTicketsService cinemaTicketsService;

    @BeforeEach
    void setUp() {
        TicketPriceCalculator ticketPriceCalculator = new TicketPriceCalculator();
        TicketPurchaseValidator ticketPurchaseValidator = new TicketPurchaseValidator();
        TicketPurchaseCalculator ticketPurchaseCalculator =
                new TicketPurchaseCalculator(ticketPriceCalculator);
        cinemaTicketsService =
                new CinemaTicketsServiceImpl(
                        paymentService,
                        seatReservationService,
                        ticketPurchaseValidator,ticketPurchaseCalculator);
    }

    @Test
    void givenOneAdultTicket_whenPurchasingTickets_thenChargesAdultPriceAndReservesOneSeat() {

        Long accountId = 123L;
        TicketRequest adultTicket = new TicketRequest(ADULT, 1);

        cinemaTicketsService.purchaseTickets(accountId, adultTicket);

        verify(paymentService)
                .debitAccount(accountId, new BigDecimal("25.99"));

        verify(seatReservationService)
                .reserveSeats(accountId, 1L);
    }

    @Test
    void givenAdultAndChildTickets_whenPurchasingTickets_thenChargesCorrectPriceAndReservesTwoSeats() {

        Long accountId = 123L;

        TicketRequest adultTicket = new TicketRequest(ADULT, 1);
        TicketRequest childTicket = new TicketRequest(CHILD, 1);

        cinemaTicketsService.purchaseTickets(
                accountId,
                adultTicket,
                childTicket
        );

        verify(paymentService)
                .debitAccount(accountId, new BigDecimal("43.49"));

        verify(seatReservationService)
                .reserveSeats(accountId, 2L);
    }

    @Test
    void givenAdultAndInfantTickets_whenPurchasingTickets_thenInfantIsFreeAndDoesNotReserveASeat() {

        Long accountId = 123L;

        TicketRequest adultTicket = new TicketRequest(ADULT, 1);
        TicketRequest infantTicket = new TicketRequest(INFANT, 1);

        cinemaTicketsService.purchaseTickets(
                accountId,
                adultTicket,
                infantTicket
        );

        verify(paymentService)
                .debitAccount(accountId, new BigDecimal("25.99"));

        verify(seatReservationService)
                .reserveSeats(accountId, 1L);
    }


    @Test
    void givenChildTicketWithoutAdult_whenPurchasingTickets_thenThrowsInvalidBookingException() {

        Long accountId = 123L;
        TicketRequest childTicket = new TicketRequest(CHILD, 1);

        assertThrows(
                InvalidBookingException.class,
                () -> cinemaTicketsService.purchaseTickets(accountId, childTicket)
        );

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void givenInfantTicketWithoutAdult_whenPurchasingTickets_thenThrowsInvalidBookingException() {

        Long accountId = 123L;
        TicketRequest infantTicket = new TicketRequest(INFANT, 1);

        assertThrows(
                InvalidBookingException.class,
                () -> cinemaTicketsService.purchaseTickets(accountId, infantTicket)
        );

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void givenMoreInfantsThanAdults_whenPurchasingTickets_thenThrowsInvalidBookingException() {

        Long accountId = 123L;

        TicketRequest adultTicket = new TicketRequest(ADULT, 1);
        TicketRequest infantTicket = new TicketRequest(INFANT, 2);

        assertThrows(
                InvalidBookingException.class,
                () -> cinemaTicketsService.purchaseTickets(
                        accountId,
                        adultTicket,
                        infantTicket
                )
        );

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void givenMoreThanTwentyFiveTickets_whenPurchasingTickets_thenThrowsInvalidBookingException() {

        Long accountId = 123L;
        TicketRequest adultTicket = new TicketRequest(ADULT, 26);

        assertThrows(
                InvalidBookingException.class,
                () -> cinemaTicketsService.purchaseTickets(accountId, adultTicket)
        );

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void givenExactlyTwentyFiveTickets_whenPurchasingTickets_thenPurchaseIsSuccessful() {

        Long accountId = 123L;
        TicketRequest adultTicket = new TicketRequest(ADULT, 25);

        cinemaTicketsService.purchaseTickets(accountId, adultTicket);

        verify(paymentService)
                .debitAccount(accountId, new BigDecimal("649.75"));

        verify(seatReservationService)
                .reserveSeats(accountId, 25L);
    }

    @Test
    void givenNegativeTicketCount_whenPurchasingTickets_thenThrowsInvalidBookingException() {

        Long accountId = 123L;
        TicketRequest adultTicket = new TicketRequest(ADULT, -1);

        assertThrows(
                InvalidBookingException.class,
                () -> cinemaTicketsService.purchaseTickets(accountId, adultTicket)
        );

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void givenZeroTotalTickets_whenPurchasingTickets_thenThrowsInvalidBookingException() {

        Long accountId = 123L;
        TicketRequest adultTicket = new TicketRequest(ADULT, 0);

        assertThrows(
                InvalidBookingException.class,
                () -> cinemaTicketsService.purchaseTickets(accountId, adultTicket)
        );

        verifyNoInteractions(paymentService, seatReservationService);
    }
    @Test
    void givenZeroAccountId_whenPurchasingTickets_thenThrowsInvalidBookingException() {

        Long accountId = 0L;
        TicketRequest adultTicket = new TicketRequest(ADULT, 1);

        assertThrows(
                InvalidBookingException.class,
                () -> cinemaTicketsService.purchaseTickets(accountId, adultTicket)
        );

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void givenNullTicketRequests_whenPurchasingTickets_thenThrowsInvalidBookingException() {

        Long accountId = 123L;

        assertThrows(
                InvalidBookingException.class,
                () -> cinemaTicketsService.purchaseTickets(
                        accountId,
                        (TicketRequest[]) null
                )
        );

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void givenNoTicketRequests_whenPurchasingTickets_thenThrowsInvalidBookingException() {

        Long accountId = 123L;

        assertThrows(
                InvalidBookingException.class,
                () -> cinemaTicketsService.purchaseTickets(accountId)
        );

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void givenNullTicketRequest_whenPurchasingTickets_thenThrowsInvalidBookingException() {

        Long accountId = 123L;

        assertThrows(
                InvalidBookingException.class,
                () -> cinemaTicketsService.purchaseTickets(
                        accountId,
                        (TicketRequest) null
                )
        );

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void givenNullTicketType_whenPurchasingTickets_thenThrowsInvalidBookingException() {

        Long accountId = 123L;
        TicketRequest ticketRequest = new TicketRequest(null, 1);

        assertThrows(
                InvalidBookingException.class,
                () -> cinemaTicketsService.purchaseTickets(
                        accountId,
                        ticketRequest
                )
        );

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void givenMultipleRequestsForSameTicketType_whenPurchasingTickets_thenAggregatesTickets() {

        Long accountId = 123L;

        TicketRequest firstAdultRequest =
                new TicketRequest(ADULT, 1);

        TicketRequest secondAdultRequest =
                new TicketRequest(ADULT, 2);

        cinemaTicketsService.purchaseTickets(
                accountId,
                firstAdultRequest,
                secondAdultRequest
        );

        verify(paymentService)
                .debitAccount(
                        accountId,
                        new BigDecimal("77.97")
                );

        verify(seatReservationService)
                .reserveSeats(accountId, 3L);
    }

    @Test
    void givenMixedTicketTypes_whenPurchasingTickets_thenChargesCorrectAmountAndReservesCorrectSeats() {

        Long accountId = 123L;

        TicketRequest adultTickets = new TicketRequest(ADULT, 2);
        TicketRequest childTickets = new TicketRequest(CHILD, 2);
        TicketRequest infantTickets = new TicketRequest(INFANT, 1);

        cinemaTicketsService.purchaseTickets(
                accountId,
                adultTickets,
                childTickets,
                infantTickets
        );

        verify(paymentService)
                .debitAccount(
                        accountId,
                        new BigDecimal("86.98")
                );

        verify(seatReservationService)
                .reserveSeats(accountId, 4L);
    }
}