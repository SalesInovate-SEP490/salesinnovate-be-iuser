package fpt.capstone.iUser.dto.request.event;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class EventAssigneeDTO {
    private Long eventAssigneeId;
    private Long eventId;
    private String userId;
}
