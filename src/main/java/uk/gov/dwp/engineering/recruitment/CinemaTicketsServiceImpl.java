package uk.gov.dwp.engineering.recruitment;

import org.springframework.stereotype.Service;
import uk.gov.dwp.engineering.recruitment.domain.BookingConfirmation;
import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;
import uk.gov.dwp.engineering.recruitment.thirdparty.PaymentService;
import uk.gov.dwp.engineering.recruitment.thirdparty.SeatReservationService;

@Service
public class CinemaTicketsServiceImpl implements CinemaTicketsService {


  private final PaymentService paymentService;
  private final SeatReservationService seatReservationService;
  private final TicketPurchaseValidator ticketPurchaseValidator;
  private final TicketPurchaseCalculator ticketPurchaseCalculator;

  public CinemaTicketsServiceImpl(PaymentService paymentService,
                                  SeatReservationService seatReservationService,
                                  TicketPurchaseValidator ticketPurchaseValidator, TicketPurchaseCalculator ticketPurchaseCalculator)
  {
    this.paymentService = paymentService;
    this.seatReservationService = seatReservationService;
    this.ticketPurchaseValidator = ticketPurchaseValidator;
    this.ticketPurchaseCalculator = ticketPurchaseCalculator;
  }

  @Override
  public BookingConfirmation purchaseTickets( final Long accountId,final TicketRequest... ticketRequests)
          throws InvalidBookingException {

    ticketPurchaseValidator.validateAccount(accountId);
    ticketPurchaseValidator.validateTicketRequests(ticketRequests);

    TicketPurchaseSummary summary = ticketPurchaseCalculator.calculate(ticketRequests);

    ticketPurchaseValidator.validateBusinessRules(summary);

    paymentService.debitAccount(accountId,summary.totalPrice());

    seatReservationService.reserveSeats( accountId, summary.seatCount());

    return new BookingConfirmation(accountId);
  }
}
