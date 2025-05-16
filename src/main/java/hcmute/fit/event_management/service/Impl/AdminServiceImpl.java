package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.DashboardStatsDTO;
import hcmute.fit.event_management.dto.EventDTO;
import hcmute.fit.event_management.dto.TransactionDTO;
import hcmute.fit.event_management.entity.*;
import hcmute.fit.event_management.repository.*;
import hcmute.fit.event_management.service.IEventService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl {

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

    @Autowired
    UserRepository userRepo;
    @Autowired
    IEventService eventService;

    public DashboardStatsDTO getDashboardStats() {
        int currentMonth = LocalDate.now().getMonthValue(); // Tháng hiện tại: 5 (May 2025)
        int previousMonth = currentMonth == 1 ? 12 : currentMonth - 1;
        int year = LocalDate.now().getYear(); // Năm hiện tại: 2025
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
        List<Transaction> transactions = transactionRepo.findAll();
        List<TransactionDTO> transactionDTOS = new ArrayList<>();
        for (Transaction transaction : transactions) {
            TransactionDTO transactionDTO = new TransactionDTO();
            BeanUtils.copyProperties(transaction, transactionDTO);
            transactionDTOS.add(transactionDTO);
        }

        Double totalRevenue = transactionRepo.getRevenue();
        Double currentRevenue = transactionRepo.getRevenueByMonth(currentYearMonth);
        Double previousRevenue = transactionRepo.getRevenueByMonth(previousYearMonth);
        String revenueChange = calculateChangePercentage(currentRevenue != null ? currentRevenue : 0,
                previousRevenue != null ? previousRevenue : 0);

        // 4. Tổng vé đã bán
        Long totalTicketsSold = bookingDetailsRepo.countTotalTicketsSold();
        Long currentTicketsSold = bookingDetailsRepo.countTicketsSoldByMonth(currentMonth, year);
        Long previousTicketsSold = bookingDetailsRepo.countTicketsSoldByMonth(previousMonth, prevYear);
        String ticketChange = calculateChangePercentage(
                currentTicketsSold != null ? currentTicketsSold : 0,
                previousTicketsSold != null ? previousTicketsSold : 0
        );

        // 5. Organizer theo tháng
        long currentOrganizers = organizerRepo.countOrganizersByMonth(currentMonth, year);
        long previousOrganizers = organizerRepo.countOrganizersByMonth(previousMonth, prevYear);
        String organizerChange = calculateChangePercentage(currentOrganizers, previousOrganizers);
        long totalOrganizers = organizerRepo.count();

        // 6. Lấy danh sách sự kiện từ eventRepo
        List<Event> events = eventRepo.findAll();
        List<EventDTO> eventDTOs = eventService.getAllEvent();
        // 7. Tính các chỉ số mới
        // 7.1 Total Revenue YTD (Doanh thu từ đầu năm 2025 đến nay)
        double totalRevenueYTD = 0.0;
        for (int month = 1; month <= currentMonth; month++) { // Từ tháng 1 đến tháng 5
            String yearMonth = String.format("%04d%02d", year, month);
            Double revenue = transactionRepo.getRevenueByMonth(yearMonth);
            totalRevenueYTD += revenue != null ? revenue : 0.0;
        }

        // 7.2 Average Ticket Price (Giá vé trung bình)
        Double averageTicketPrice = totalTicketsSold != null && totalTicketsSold > 0 ?
                (totalRevenue != null ? totalRevenue : 0.0) / totalTicketsSold : 0.0;

        // 7.3 Refund Rate (Tỷ lệ hoàn tiền)
        long totalRefundAmount = transactions.stream()
                .filter(t -> ("REFUND").equals(t.getTransactionStatus()))
                .count();
        double refundRate = !transactions.isEmpty() ? (double) totalRefundAmount / (long) transactions.size() * 100 : 0;

        // 7.4 New Organizers This Month (Số tổ chức mới trong tháng)
        long newOrganizersThisMonth = organizerRepo.countOrganizersByMonth(currentMonth, year);

        // 7.5 Booking Conversion Rate (Tỷ lệ chuyển đổi đặt chỗ)
        long confirmedBookings = events.stream()
                .flatMap(e -> e.getBookings().stream())
                .filter(b -> "PAID".equals(b.getBookingStatus()))
                .count();
        Double bookingConversionRate = totalBookings > 0 ?
                (confirmedBookings / (double) totalBookings * 100) : 0.0;

        // 7.6 Top Event Category (Loại sự kiện có doanh thu cao nhất)
        Map<String, Double> revenueByCategory = events.stream()
                .collect(Collectors.toMap(
                        Event::getEventType,
                        event -> event.getBookings().stream()
                                .filter(b -> "PAID".equals(b.getBookingStatus()))
                                .mapToDouble(b -> b.getTransaction() != null ? b.getTransaction().getTransactionAmount() : 0.0)
                                .sum(),
                        Double::sum
                ));
        String topEventCategory = revenueByCategory.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        // 7.7 User Engagement Score (Điểm tương tác người dùng, dựa trên CheckInTicket)
        long userEngagementScore = userRepo.findAll().stream()
                .mapToLong(user -> user.getListBooking().stream()
                        .flatMap(booking -> booking.getBookingDetails().stream())
                        .mapToLong(bd -> bd.getCheckInTickets().size())
                        .sum())
                .sum();

        // 7.8 Total Active Events (Số sự kiện đang hoạt động)
        long totalActiveEvents = eventDTOs.stream()
                .filter(e -> "Active".equals(e.getEventStatus()))
                .count();

        return new DashboardStatsDTO(
                totalEvents, currentEvents, eventChange,
                totalBookings, currentBookings, bookingChange,
                totalRevenue, currentRevenue != null ? currentRevenue : 0.0, revenueChange,
                totalOrganizers, currentOrganizers, organizerChange,
                totalTicketsSold, currentTicketsSold != null ? currentTicketsSold : 0L, ticketChange,
                eventDTOs, transactionDTOS,
                totalRevenueYTD, averageTicketPrice, refundRate,
                newOrganizersThisMonth, bookingConversionRate,
                topEventCategory, userEngagementScore,
                totalActiveEvents
        );
    }

    public String calculateChangePercentage(double current, double previous) {
        if (previous == 0) return current == 0 ? "0%" : "+100%";
        double change = ((current - previous) / previous) * 100;
        return (change >= 0 ? "+" : "") + String.format("%.1f", change) + "%";
    }
}