package uk.gov.dwp.engineering.recruitment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import uk.gov.dwp.engineering.recruitment.domain.BookingConfirmation;
import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;
import uk.gov.dwp.engineering.recruitment.thirdparty.PaymentService;
import uk.gov.dwp.engineering.recruitment.thirdparty.SeatReservationService;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



@WebMvcTest(CinemaTicketsController.class)
@Import({
        CinemaTicketsServiceImpl.class,
        TicketPurchaseValidator.class,
        TicketPurchaseCalculator.class,
        TicketPriceCalculator.class
})
class CinemaTicketsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private SeatReservationService seatReservationService;

    @Test
    void givenValidBooking_whenMakingBooking_thenReturnsCreated()
            throws Exception {

        mockMvc.perform(
                        post("/cinema/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                              "accountId": 123,
                              "ticketRequests": [
                                {
                                  "type": "ADULT",
                                  "ticketCount": 1
                                }
                              ]
                            }
                            """)
                )
                .andExpect(status().isCreated());

        verify(paymentService)
                .debitAccount(
                        123L,
                        new BigDecimal("25.99")
                );

        verify(seatReservationService)
                .reserveSeats(123L, 1L);
    }

    @Test
    void givenChildWithoutAdult_whenMakingBooking_thenReturnsBadRequest()
            throws Exception {

        mockMvc.perform(
                        post("/cinema/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                              "accountId": 123,
                              "ticketRequests": [
                                {
                                  "type": "CHILD",
                                  "ticketCount": 1
                                }
                              ]
                            }
                            """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail")
                        .value("Child tickets cannot be purchased without an adult ticket"));

        verifyNoInteractions(
                paymentService,
                seatReservationService
        );
    }

    @Test
    void givenMoreThanTwentyFiveTickets_whenMakingBooking_thenReturnsBadRequest()
            throws Exception {

        mockMvc.perform(
                        post("/cinema/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                              "accountId": 123,
                              "ticketRequests": [
                                {
                                  "type": "ADULT",
                                  "ticketCount": 26
                                }
                              ]
                            }
                            """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail")
                        .value("A maximum of 25 tickets can be purchased"));

        verifyNoInteractions(
                paymentService,
                seatReservationService
        );
    }
    @Test
    void givenInfantWithoutAdult_whenMakingBooking_thenReturnsBadRequest()
            throws Exception {

        mockMvc.perform(
                        post("/cinema/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                              "accountId": 123,
                              "ticketRequests": [
                                {
                                  "type": "INFANT",
                                  "ticketCount": 1
                                }
                              ]
                            }
                            """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail")
                        .value("The number of infant tickets cannot exceed the number of adult tickets"));

        verifyNoInteractions(
                paymentService,
                seatReservationService
        );
    }


    @Test
    void givenInvalidTicketType_whenMakingBooking_thenReturnsBadRequest()
            throws Exception {

        mockMvc.perform(
                        post("/cinema/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                              "accountId": 123,
                              "ticketRequests": [
                                {
                                  "type": "INVALID",
                                  "ticketCount": 1
                                }
                              ]
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(
                paymentService,
                seatReservationService
        );
    }
}