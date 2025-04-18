package hcmute.fit.event_management.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private String bookingId;
    private String totalPrice;
    private String bookingDate;
    private String bookingStatus;
    private String userId;
}
