package hcmute.fit.event_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {
    private long totalEvents;
    private long totalEventsThisMonth;
    private String totalEventsChange;

    private long totalBookings;
    private long totalBookingsThisMonth;
    private String totalBookingsChange;

    private Double totalRevenue;
    private Double totalRevenueThisMonth;
    private String totalRevenueChange;

    private long totalOrganizers;
    private long totalOrganizersThisMonth;
    private String totalOrganizersChange;

    private Long totalTicketsSold;
    private Long totalTicketsSoldThisMonth;
    private String totalTicketsSoldChange;

    private List<EventDTO> events;
    private List<TransactionDTO> transactions;

    private Double totalRevenueYTD;
    private Double averageTicketPrice;
    private Double refundRate;
    private long newOrganizersThisMonth;
    private Double bookingConversionRate;
    private String topEventCategory;
    private long userEngagementScore;
    private long totalActiveEvents; // Thêm trường mới
}

