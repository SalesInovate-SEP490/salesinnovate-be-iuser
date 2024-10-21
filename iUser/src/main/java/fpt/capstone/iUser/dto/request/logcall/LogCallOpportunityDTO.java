package fpt.capstone.iUser.dto.request.logcall;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LogCallOpportunityDTO {
    private Long logCallOpportunitiesId;
    private Long opportunityId;
    private Long logCallId;
}
