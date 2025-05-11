package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.DashboardStatsDTO;
import hcmute.fit.event_management.dto.EventDTO;
import hcmute.fit.event_management.entity.Booking;
import hcmute.fit.event_management.entity.BookingDetails;
import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.repository.*;
import hcmute.fit.event_management.service.IAdminService;
import hcmute.fit.event_management.service.IEventService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl  {
    @Autowired
    EventRepository eventRepo;

    @Autowired
    BookingRepository bookingRepo;
    @Autowired
    TransactionRepository transactionRepo;
    @Autowired
    BookingDetailsRepository bookingDetailsRepo;
    @Autowired
    OrganizerRepository organizerRepo;
    public DashboardStatsDTO getDashboardStats() {
        int currentMonth = LocalDate.now().getMonthValue();
        int previousMonth = currentMonth == 1 ? 12 : currentMonth - 1;
        int year = LocalDate.now().getYear();
        int prevYear = currentMonth == 1 ? year - 1 : year;

        // 1. Tổng số sự kiện
        long totalEvents = eventRepo.count();
        long currentEvents = eventRepo.countEventsByMonth(currentMonth, year);
        long previousEvents = eventRepo.countEventsByMonth(previousMonth, prevYear);
        String eventChange = calculateChangePercentage(currentEvents, previousEvents);

        // 2. Tổng đơn đặt
        long totalBookings = bookingRepo.countBookings();
        long currentBookings = bookingRepo.countBookingsByMonth(currentMonth, year);
        long previousBookings = bookingRepo.countBookingsByMonth(previousMonth, prevYear);
        String bookingChange = calculateChangePercentage(currentBookings, previousBookings);

        // 3. Tổng doanh thu

        String currentYearMonth = String.format("%04d%02d", year, currentMonth);
        String previousYearMonth = String.format("%04d%02d", prevYear, previousMonth);
        Double totalRevenue = transactionRepo.getRevenue();
        Double currentRevenue = transactionRepo.getRevenueByMonth(currentYearMonth);
        Double previousRevenue = transactionRepo.getRevenueByMonth(previousYearMonth);
        String revenueChange = calculateChangePercentage(currentRevenue != null ? currentRevenue : 0,
                previousRevenue != null ? previousRevenue : 0);

        // Organizer theo tháng

        long currentOrganizers = organizerRepo.countOrganizersByMonth(currentMonth, year);
        long previousOrganizers = organizerRepo.countOrganizersByMonth(previousMonth, prevYear);
        String organizerChange = calculateChangePercentage(currentOrganizers, previousOrganizers);
        long totalOrganizers = organizerRepo.count();

        // 4. Tổng vé đã bán
        Long totalTickets = bookingDetailsRepo.countTotalTicketsSold();
        Long currentTicketsSold = bookingDetailsRepo.countTicketsSoldByMonth(currentMonth, year);
        Long previousTicketsSold = bookingDetailsRepo.countTicketsSoldByMonth(previousMonth, prevYear);
        String ticketChange = calculateChangePercentage(
                currentTicketsSold != null ? currentTicketsSold : 0,
                previousTicketsSold != null ? previousTicketsSold : 0
        );
        List<Event> events = eventRepo.findAll();
        List<EventDTO> eventDTOS = new ArrayList<>();
        for (Event event : events){
            EventDTO eventDTO = new EventDTO();
            BeanUtils.copyProperties(event, eventDTO);
            int sold = 0;
            for (Booking b : event.getBookings()){
                if (b.getBookingStatus().equals("PAID")) {
                    for (BookingDetails bd : b.getBookingDetails()) {
                        sold += bd.getQuantity();
                    }
                }
            }
            eventDTO.setSold(sold);
            eventDTOS.add(eventDTO);
        }
        return new DashboardStatsDTO(
                totalEvents, currentEvents, eventChange,
                totalBookings, currentBookings, bookingChange,
                totalRevenue, currentRevenue != null ? currentRevenue : 0.0, revenueChange,
                totalOrganizers, currentOrganizers, organizerChange,
                totalTickets, currentTicketsSold != null ? currentTicketsSold : 0L, ticketChange, eventDTOS
        );
    }

    public String calculateChangePercentage(double current, double previous) {
        if (previous == 0) return current == 0 ? "0%" : "+100%";
        double change = ((current - previous) / previous) * 100;
        return (change >= 0 ? "+" : "") + String.format("%.1f", change) + "%";
    }

}
