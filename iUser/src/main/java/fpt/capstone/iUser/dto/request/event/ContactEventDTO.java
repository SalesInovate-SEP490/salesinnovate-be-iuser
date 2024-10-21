package fpt.capstone.iUser.dto.request.event;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ContactEventDTO {
    private Long contactEventId;
    private Long eventId;
    private Long contactId;
}
