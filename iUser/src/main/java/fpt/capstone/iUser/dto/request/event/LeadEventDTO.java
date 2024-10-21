package fpt.capstone.iUser.dto.request.event;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LeadEventDTO {
    private Long leadEventId;
    private Long eventId;
    private Long leadId;
}
