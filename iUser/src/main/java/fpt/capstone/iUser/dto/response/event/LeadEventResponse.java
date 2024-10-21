package fpt.capstone.iUser.dto.response.event;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeadEventResponse {
    private Long leadEventId;
    private Long leadId;
    private String leadName;
    private Long eventId;
}
