package fpt.capstone.iUser.dto.request.logcall;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LogCallLeadsDTO {
    private Long logCallLeadsId;
    private Long leadId;
    private Long logCallId;
}
