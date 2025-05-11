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

    private double totalRevenue;
    private double totalRevenueThisMonth;
    private String totalRevenueChange;

    private long totalOrganizers;
    private long totalOrganizersThisMonth;
    private String totalOrganizersChange;

    private long totalTicketsSold;
    private long totalTicketsSoldThisMonth;
    private String totalTicketsSoldChange;
    private List<EventDTO> events;
}

