package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.OrderTicketDTO;
import hcmute.fit.event_management.entity.BookingDetails;
import hcmute.fit.event_management.repository.BookingDetailsRepository;
import hcmute.fit.event_management.service.IBookingDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingDetailsServiceImpl implements IBookingDetailsService {
    @Autowired
    private BookingDetailsRepository bookingDetailsRepository;

    public Optional<BookingDetails> findById(Integer integer) {
        return bookingDetailsRepository.findById(integer);
    }
    @Override
    public List<OrderTicketDTO> getOrdersTicket(int ticketId){
        List<BookingDetails> bookingDetails = bookingDetailsRepository.findByTicketId(ticketId);
        List<OrderTicketDTO> orderTicketDTOs = new ArrayList<>();
        for (BookingDetails bookingDetail : bookingDetails) {
            OrderTicketDTO orderTicketDTO = new OrderTicketDTO();
            orderTicketDTO.setOrderID(bookingDetail.getBookingDetailsId());
            orderTicketDTO.setTicketName(bookingDetail.getTicket().getTicketName());
            orderTicketDTO.setQuantity(bookingDetail.getQuantity());
            orderTicketDTO.setAmount(bookingDetail.getPrice() * bookingDetail.getQuantity());
            orderTicketDTO.setDateOrdered(bookingDetail.getBooking().getBookingDate());
            orderTicketDTOs.add(orderTicketDTO);
        }
        return orderTicketDTOs;
    }
}
