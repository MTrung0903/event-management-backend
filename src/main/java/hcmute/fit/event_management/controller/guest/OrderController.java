package hcmute.fit.event_management.controller.guest;

import com.cloudinary.Cloudinary;
import hcmute.fit.event_management.dto.*;
import hcmute.fit.event_management.entity.*;
import hcmute.fit.event_management.service.IBookingDetailsService;
import hcmute.fit.event_management.service.IBookingService;
import hcmute.fit.event_management.service.ITransactionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payload.Response;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/booking")
public class OrderController {
    @Autowired
    IBookingService bookingService;
    @Autowired
    IBookingDetailsService bookingDetailsService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    private Cloudinary cloudinary;
    @GetMapping("/{userId}")
    public ResponseEntity<?> getOrder(@PathVariable("userId") int userId) {
        List<Booking> bookings = bookingService.findByUserId(userId);

        // Lọc các đơn hết hạn & chưa thanh toán để xóa
        List<Booking> expiredUnpaidBookings = bookings.stream()
                .filter(b -> b.getExpireDate().before(new Date()) && !"PAID".equals(b.getBookingStatus()))
                .collect(Collectors.toList());

        // Chỉ lấy các đơn đã thanh toán
        List<Booking> paidBookings = bookings.stream()
                .filter(b -> "PAID".equals(b.getBookingStatus()))
                .toList();

        // Xóa các đơn hết hạn chưa thanh toán
        bookingService.deleteAll(expiredUnpaidBookings);

        // Chuyển đổi sang MyOrderDTO
        List<MyOrderDTO> myOrderDTOList = paidBookings.stream().map(booking -> {
            MyOrderDTO myOrderDTO = new MyOrderDTO();

            // Set thông tin giao dịch nếu có
            transactionService.findByOrderCode(booking.getBookingCode()).ifPresent(transaction -> {
                TransactionDTO transactionDTO = new TransactionDTO();
                BeanUtils.copyProperties(transaction, transactionDTO);
                myOrderDTO.setTransaction(transactionDTO);
            });

            // Set thông tin sự kiện
            Event event = booking.getEvent();
            EventLocation eventLocation = event.getEventLocation();
            EventLocationDTO eventLocationDTO = new EventLocationDTO();
            BeanUtils.copyProperties(eventLocation, eventLocationDTO);
            EventDTO eventDTO = new EventDTO();
            eventDTO.setEventLocation(eventLocationDTO);
            BeanUtils.copyProperties(event, eventDTO);
            System.out.println("<<<<<<<<<<<<<" + eventDTO + ">>>>>>>>>>>>>>>>>");
            List<String> imageUrls = event.getEventImages().stream()
                    .map(cloudinary.url()::generate)
                    .collect(Collectors.toList());
            eventDTO.setEventImages(imageUrls);
            myOrderDTO.setEvent(eventDTO);

            // Set danh sách vé
            List<TicketDTO> ticketDTOS = booking.getBookingDetails().stream().map(detail -> {
                TicketDTO ticketDTO = new TicketDTO();
                BeanUtils.copyProperties(detail.getTicket(), ticketDTO);
                ticketDTO.setQuantity(detail.getQuantity());
                return ticketDTO;
            }).collect(Collectors.toList());
            myOrderDTO.setTickets(ticketDTOS);

            myOrderDTO.setOrderId(booking.getBookingCode());
            return myOrderDTO;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(myOrderDTOList);
    }
    @GetMapping("/{userId}/has-bought-free-ticket/{eventId}")
    public ResponseEntity<Response> checkBoughtFreeTicket(@PathVariable("userId") int userId, @PathVariable("eventId") int eventId) {
        boolean hasBoughtFreeTicket = bookingService.hasBoughtFreeTicket(userId, eventId);
        Response response = new Response();
        response.setData(hasBoughtFreeTicket);
        if(hasBoughtFreeTicket) response.setMsg("Has bought free ticket");
        else response.setMsg("Has not bought free ticket");
        response.setStatusCode(200);
        return ResponseEntity.ok(response);
    }
}
