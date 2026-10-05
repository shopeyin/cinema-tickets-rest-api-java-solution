package uk.gov.dwp.engineering.recruitment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import uk.gov.dwp.engineering.recruitment.domain.BookingConfirmation;
import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CinemaTicketsController.class)
class CinemaTicketsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CinemaTicketsService cinemaTicketsService;

    @Test
    void givenValidBooking_whenMakingBooking_thenReturnsCreated()
            throws Exception {

        Long accountId = 123L;

        when(cinemaTicketsService.purchaseTickets(
                eq(accountId),
                any(TicketRequest[].class)
        )).thenReturn(new BookingConfirmation(accountId));

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
    }

    @Test
    void givenInvalidBooking_whenMakingBooking_thenReturnsBadRequest()
            throws Exception {

        Long accountId = 123L;

        when(cinemaTicketsService.purchaseTickets(
                eq(accountId),
                any(TicketRequest[].class)
        )).thenThrow(
                new InvalidBookingException(
                        "Child tickets cannot be purchased without an adult ticket"
                )
        );

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
                .andExpect(jsonPath("$.detail")
                        .value("Child tickets cannot be purchased without an adult ticket"));
    }
}