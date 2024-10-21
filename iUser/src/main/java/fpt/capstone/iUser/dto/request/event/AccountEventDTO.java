package fpt.capstone.iUser.dto.request.event;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class AccountEventDTO {
    private Long accountEventId;
    private Long eventId;
    private Long accountId;
}
